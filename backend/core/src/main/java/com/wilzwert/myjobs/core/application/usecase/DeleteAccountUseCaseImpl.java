package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserDeleteException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;

/**
 * @author Wilhelm Zwertvaegher
 */

public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {
    private final UserDataManager userDataManager;

    private final FileStorage fileStorage;


    public DeleteAccountUseCaseImpl(UserDataManager userDataManager, FileStorage fileStorage) {
        this.userDataManager = userDataManager;
        this.fileStorage = fileStorage;
    }

    @Override
    public void deleteAccount(UserId userId) {
        User user = userDataManager.findById(userId).orElseThrow(UserNotFoundException::new);

        // delete attachment's files
        // FIXME : this could (should) maybe be done in a cleaner way
        for (Job job : user.getJobs()) {
            for(Attachment attachment : job.getAttachments()) {
                try {
                    fileStorage.delete(attachment.getFileId());
                } catch (Exception e) {
                    // TODO log incoherence
                    throw new UserDeleteException();
                }
            }
        }

        userDataManager.deleteUser(user);

        // TODO : send a deletion confirmation email
    }
}
