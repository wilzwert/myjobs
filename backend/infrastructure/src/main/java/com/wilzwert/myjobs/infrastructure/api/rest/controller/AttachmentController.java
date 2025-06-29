package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.AttachmentFileInfo;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentsCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.GetAttachmentFileInfoUseCase;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.AddAttachmentToJobUseCase;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.DeleteAttachmentUseCase;
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.DownloadAttachmentUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.mapper.AttachmentFileInfoMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.AttachmentMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import com.wilzwert.myjobs.infrastructure.storage.StorageException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class AttachmentController {

    private final AttachmentMapper attachmentMapper;

    private final AddAttachmentToJobUseCase addAttachmentToJobUseCase;

    private final DownloadAttachmentUseCase downloadAttachmentUseCase;

    private final DeleteAttachmentUseCase deleteAttachmentUseCase;

    private final GetAttachmentFileInfoUseCase getAttachmentFileInfoUseCase;

    private final AttachmentFileInfoMapper attachmentFileInfoMapper;


    public AttachmentController(
            AddAttachmentToJobUseCase addAttachmentToJobUseCase,
            DownloadAttachmentUseCase downloadAttachmentUseCase,
            DeleteAttachmentUseCase deleteAttachmentUseCase,
            GetAttachmentFileInfoUseCase getAttachmentFileInfoUseCase,
            AttachmentMapper attachmentMapper,
            AttachmentFileInfoMapper attachmentFileInfoMapper
    ) {
        this.addAttachmentToJobUseCase = addAttachmentToJobUseCase;
        this.downloadAttachmentUseCase = downloadAttachmentUseCase;
        this.deleteAttachmentUseCase = deleteAttachmentUseCase;
        this.getAttachmentFileInfoUseCase = getAttachmentFileInfoUseCase;
        this.attachmentMapper = attachmentMapper;
        this.attachmentFileInfoMapper = attachmentFileInfoMapper;
    }

    @PostMapping("{jobId}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AttachmentResponse> createAttachment(@PathVariable("jobId") String jobId, @RequestBody @Valid List<CreateAttachmentRequest> createAttachmentsRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // put files contents in temp files and create command
        // (this should not be done here !)
        List<File> tempFiles = new ArrayList<>();
        List<Attachment> attachments;
        try {
            List<CreateAttachmentCommand> createAttachmentCommands = new ArrayList<>();

            for(CreateAttachmentRequest createAttachmentRequest : createAttachmentsRequest) {
                File tempFile = File.createTempFile("atta", "chment");

                String[] parts = createAttachmentRequest.getContent().split(",");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid base64 content");
                }
                byte[] fileData = Base64.getDecoder().decode(parts[1]);
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(fileData);
                }

                tempFiles.add(tempFile);
                createAttachmentCommands.add(attachmentMapper.toCommand(createAttachmentRequest, tempFile));
            }
            attachments = addAttachmentToJobUseCase.addAttachmentsToJob(new CreateAttachmentsCommand(createAttachmentCommands, userDetails.getId(), new JobId(UUID.fromString(jobId))));

        }
        catch (IOException e) {
            throw new StorageException("an io exception occurred", e);
        }

        try {
            for(File tempFile: tempFiles) {
                Files.delete(tempFile.toPath());
            }
        }
        catch (IOException e) {
            log.error("An io exception occurred while deleting attachment temp  file", e);
            throw new StorageException("an io exception occurred", e);
        }

        return attachmentMapper.toResponse(attachments);
    }

    @DeleteMapping("{jobId}/attachments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttachment(@PathVariable("jobId") String jobId, @PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        deleteAttachmentUseCase.deleteAttachment(new DeleteAttachmentCommand(new AttachmentId(UUID.fromString(id)), userDetails.getId(), new JobId(UUID.fromString(jobId))));
    }

    @GetMapping("{jobId}/attachments/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("jobId") String jobId, @PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DownloadableFile downloadableFile = downloadAttachmentUseCase.downloadAttachment(new DownloadAttachmentCommand(id, userDetails.getId(), new JobId(UUID.fromString(jobId))));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadableFile.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadableFile.filename() + "\"")
                .body(new FileSystemResource(downloadableFile.path()));
    }

    @GetMapping("{jobId}/attachments/{id}/file/info")
    public ProtectedFileResponse getProtectedFile(@PathVariable("jobId") String jobId, @PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        AttachmentFileInfo info = getAttachmentFileInfoUseCase.getAttachmentFileInfo(new DownloadAttachmentCommand(id, userDetails.getId(), new JobId(UUID.fromString(jobId))));
        return attachmentFileInfoMapper.toResponse(info);
    }
}