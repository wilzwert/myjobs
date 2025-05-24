package com.wilzwert.myjobs.core.domain.model.attachment;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 */

class AttachmentTest {

    @Test
    void shouldCreateAttachment() {
        AttachmentId attachmentId = AttachmentId.generate();
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .name("attachment")
                .fileId("fileId")
                .filename("filename.jpg")
                .contentType("image/jpeg")
                .build();

        assertNotNull(attachment);
        assertEquals(attachmentId, attachment.getId());
        assertEquals("attachment", attachment.getName());
        assertEquals("fileId", attachment.getFileId());
        assertEquals("filename.jpg", attachment.getFilename());
        assertEquals("image/jpeg", attachment.getContentType());
    }
}
