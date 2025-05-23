package com.wilzwert.myjobs.infrastructure.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecureTempFileHelperTest {

    SecureTempFileHelper helper = new SecureTempFileHelper();

    @Test
    void whenPosixSupported_thenShouldGetFileAttributePosixPermissions(@TempDir Path tempDir) throws IOException {
        if (helper.supportsPosix()) {
            FileAttribute<?> attr = helper.getFileAttribute();
            assertEquals("posix:permissions", attr.name());

            Path tempFile = Files.createTempFile(tempDir, "test", ".tmp", attr);
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(tempFile);
            assertEquals(PosixFilePermissions.fromString("rwx------"), perms);
        }
    }

    @Test
    void whenPosixNotSupported_thenShouldGetFileAttributeReturnsAcl(@TempDir Path tempDir) throws IOException {
        if (!helper.supportsPosix() &&
            FileSystems.getDefault().supportedFileAttributeViews().contains("acl")) {

            FileAttribute<?> attr = helper.getFileAttribute();
            assertEquals("acl:acl", attr.name());

            // Check that returned value contains at least one ACL entry for current user
            @SuppressWarnings("unchecked")
            List<AclEntry> aclEntries = (List<AclEntry>) attr.value();

            assertFalse(aclEntries.isEmpty());
            assertEquals(AclEntryType.ALLOW, aclEntries.getFirst().type());
            assertNotNull(aclEntries.getFirst().principal());

            // Optionally apply the attribute to a file to verify it doesn't throw
            Path tempFile = Files.createTempFile(tempDir, "test", ".tmp", attr);
            assertTrue(Files.exists(tempFile));
        }
    }
}
