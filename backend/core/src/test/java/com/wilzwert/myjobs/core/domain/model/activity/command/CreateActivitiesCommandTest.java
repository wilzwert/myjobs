package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateActivitiesCommandTest {

    @Test
    void builderShouldCreateCommandWithGivenFields() {
        CreateActivityCommand activity1 = new CreateActivityCommand(ActivityType.CREATION, "Job creation");
        CreateActivityCommand activity2 = new CreateActivityCommand(ActivityType.EMAIL, "Email sent");

        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();

        List<CreateActivityCommand> activities = List.of(activity1, activity2);

        CreateActivitiesCommand command = new CreateActivitiesCommand.Builder()
                .commandList(activities)
                .userId(userId)
                .jobId(jobId)
                .build();

        // Assert
        assertEquals(activities, command.createActivityCommandList());
        assertEquals(userId, command.userId());
        assertEquals(jobId, command.jobId());
    }

    @Test
    void builderShouldCopyFromExistingCommand() {
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        List<CreateActivityCommand> activities = List.of(
                new CreateActivityCommand(ActivityType.IN_PERSON_INTERVIEW, "Interview")
        );

        CreateActivitiesCommand original = new CreateActivitiesCommand(activities, userId, jobId);

        CreateActivitiesCommand copy = new CreateActivitiesCommand.Builder(original).build();

        assertEquals(original.createActivityCommandList(), copy.createActivityCommandList());
        assertEquals(original.userId(), copy.userId());
        assertEquals(original.jobId(), copy.jobId());
    }
}
