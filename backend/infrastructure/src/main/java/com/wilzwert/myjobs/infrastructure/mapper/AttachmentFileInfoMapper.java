package com.wilzwert.myjobs.infrastructure.mapper;


import com.wilzwert.myjobs.core.domain.model.AttachmentFileInfo;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ProtectedFileResponse;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 */
@Mapper(componentModel = "spring")
public interface AttachmentFileInfoMapper {
    ProtectedFileResponse toResponse(AttachmentFileInfo attachmentFileInfo);
}
