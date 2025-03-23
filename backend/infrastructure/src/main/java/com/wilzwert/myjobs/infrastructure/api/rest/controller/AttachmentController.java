package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.command.DeleteAttachmentCommand;
import com.wilzwert.myjobs.core.domain.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.exception.AttachmentNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.Attachment;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.ports.driving.AddAttachmentToJobUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.DeleteAttachmentUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.DownloadAttachmentUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.AttachmentMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:43
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class AttachmentController {

    private final AttachmentMapper attachmentMapper;

    private final AddAttachmentToJobUseCase addAttachmentToJobUseCase;

    private final DownloadAttachmentUseCase downloadAttachmentUseCase;

    private final DeleteAttachmentUseCase deleteAttachmentUseCase;


    public AttachmentController(AddAttachmentToJobUseCase addAttachmentToJobUseCase, DownloadAttachmentUseCase downloadAttachmentUseCase, DeleteAttachmentUseCase deleteAttachmentUseCase, AttachmentMapper attachmentMapper) {
        this.addAttachmentToJobUseCase = addAttachmentToJobUseCase;
        this.downloadAttachmentUseCase = downloadAttachmentUseCase;
        this.deleteAttachmentUseCase = deleteAttachmentUseCase;
        this.attachmentMapper = attachmentMapper;
    }

    @PostMapping("{jobId}/attachments")
    public AttachmentResponse createAttachment(@PathVariable("jobId") String jobId, @RequestBody CreateAttachmentRequest createAttachmentRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // put file contents in temp file
        try {
            File tempFile = File.createTempFile("atta", "chment");

            String[] parts = createAttachmentRequest.getContent().split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid base64 content");
            }
            byte[] fileData = Base64.getDecoder().decode(parts[1]);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileData);
            }

            CreateAttachmentCommand command = attachmentMapper.toCommand(createAttachmentRequest, userDetails.getId(), new JobId(UUID.fromString(jobId)), tempFile);
            Attachment attachment = addAttachmentToJobUseCase.addAttachmentToJob(command);
            tempFile.delete();
            return attachmentMapper.toResponse(attachment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("{jobId}/attachments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttachment(@PathVariable("jobId") String jobId, @PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        deleteAttachmentUseCase.deleteAttachment(new DeleteAttachmentCommand(id, userDetails.getId(), new JobId(UUID.fromString(jobId))));
    }

    @GetMapping("{jobId}/attachments/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("jobId") String jobId, @PathVariable("id") String id, Authentication authentication) throws IOException{
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DownloadableFile downloadableFile = downloadAttachmentUseCase.downloadAttachment(new DownloadAttachmentCommand(id, userDetails.getId(), new JobId(UUID.fromString(jobId))));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadableFile.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadableFile.filename() + "\"")
                .body(new FileSystemResource(downloadableFile.path()));
    }
}