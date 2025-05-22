package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserDeleteException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/03/2025
 * Time:10:43
 */

public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {
    private final UserService userService;

    private final JobService jobService;

    private final FileStorage fileStorage;


    public DeleteAccountUseCaseImpl(UserService userService, JobService jobService, FileStorage fileStorage) {
        this.userService = userService;
        this.jobService = jobService;
        this.fileStorage = fileStorage;
    }

    @Override
    public void deleteAccount(UserId userId) {
        User user = userService.findById(userId).orElseThrow(UserNotFoundException::new);

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

        userService.deleteUser(user);

        // TODO : send a deletion confirmation email
    }
}
