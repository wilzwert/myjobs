package com.wilzwert.myjobs.infrastructure.storage;


import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.*;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Helper for temp files creation
 * Temp files are by default world readable, as they are created in a common temp dir
 * To secure those files we need to make them readable and writable only by the current system user
 * It is pretty easy when the project is run on posix compatible systems (e.g. Linux)
 * but it gets a bit more complicated on Windows systems
 * The getFileAttribute generates a FileAttribute based on posix support
 */
@Component
public class SecureTempFileHelper {

    private final boolean supportsPosix;

    public SecureTempFileHelper() {
        supportsPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
    }

    public boolean supportsPosix() {
        return supportsPosix;
    }

    /**
     * Creates a FileAttribute to be passed in the temp file creation
     * based on posix support
     * At this time we have no choice but to use wildcard generics in the method return type because return types
     * differ whether system is posix compatible of not
     * @return a FileAttribute for a temp file
     * @throws IOException en exception FileAttribute cannot be created
     */
    public FileAttribute<? extends Collection<?>> getFileAttribute() throws IOException {
        if(supportsPosix) {
            return PosixFilePermissions
                    .asFileAttribute(PosixFilePermissions.fromString("rwx------"));
        }
        else {
            UserPrincipal user =
                    FileSystems.getDefault()
                            .getUserPrincipalLookupService()
                            .lookupPrincipalByName(System.getProperty("user.name"));
            List<AclEntry> acl =
                    Collections.singletonList(AclEntry.newBuilder()
                            .setType(AclEntryType.ALLOW)
                            .setPrincipal(user)
                            .setPermissions(EnumSet.allOf(AclEntryPermission.class))
                            .setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
                            .build());
            return
                    new FileAttribute<List<AclEntry>>() {
                        @Override
                        public String name() {
                            return "acl:acl";
                        }

                        @Override
                        public List<AclEntry> value() {
                            return acl;
                        }
                    };
        }
    }
}
