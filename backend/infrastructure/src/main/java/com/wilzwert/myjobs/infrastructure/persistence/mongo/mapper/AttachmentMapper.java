package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */

@Mapper(componentModel = "spring", uses = IdMapper.class)
public interface AttachmentMapper extends EntityMapper<Attachment, MongoAttachment, CreateAttachmentRequest, CreateAttachmentCommand, UpdateActivityRequest, UpdateActivityCommand, AttachmentResponse> {
    @Mapping(source = "createAttachmentRequest.name", target = "name")
    @Mapping(source = "file", target = "file")
    CreateAttachmentCommand toCommand(CreateAttachmentRequest createAttachmentRequest, UserId userId, JobId jobId, File file);
}