package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.exception.IncompleteAggregateException;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class UserDataManagerAdapterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private UserDataManagerAdapter underTest;

    @Test
    void shouldReturnAllsUsersMinimalByEmail() {
        Map<UserId, User> users = underTest.findMinimal(DomainSpecification.in("email", List.of("existing@example.com", "otherexisting@example.com")));
        assertThat(users).hasSize(2);
        assertThat(users.values().stream().toList().getFirst().getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().getFirst().getJobs());
        assertThat(users.values().stream().toList().get(1).getEmail()).isEqualTo("otherexisting@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().get(1).getJobs());
    }

    @Test
    void shouldReturnAllsUsersMinimalById() {
        Map<UserId, User> users = underTest.findMinimal(
                DomainSpecification.applySort(
                    DomainSpecification.in("id",
                            List.of(
                                    new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")),
                                    new UserId(UUID.fromString("abcd4321-4321-4321-4321-123456789012"))
                            )
                    ),
                    DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
                )
        );
        assertThat(users).hasSize(2);
        var user1 = users.values().stream().toList().getFirst();
        assertThat(user1.getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, user1::getJobs);

        var user2 = users.values().stream().toList().get(1);
        assertThat(user2.getEmail()).isEqualTo("otherexisting@example.com");
        assertThrows(IncompleteAggregateException.class, user2::getJobs);
    }

    @Test
    void shouldReturnUserViewById() {
        assertThat(underTest.findViewById(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012"))))
                .get()
                .hasFieldOrPropertyWithValue("email", "existing@example.com");

    }

    @Test
    void shouldReturnUserMinimalById() {
        Optional<User> user = underTest.findMinimalById(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThat(user.isPresent()).isTrue();
        var u  =user.get();
        assertThat(u.getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, u::getJobs);
    }

    @Test
    void shouldReturnUserMinimalByEmail() {
        Optional<User> user = underTest.findMinimalByEmail("existing@example.com");
        assertThat(user.isPresent()).isTrue();
        var u = user.get();
        assertThat(u.getId()).isEqualTo(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThrows(IncompleteAggregateException.class, u::getJobs);
    }

    @Test
    void shouldReturnUserMinimalByEmailValidationCode() {
        Optional<User> user = underTest.findMinimalByEmailValidationCode("existing-email-validation-code");
        assertThat(user.isPresent()).isTrue();
        var u = user.get();
        assertThat(u.getId()).isEqualTo(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThrows(IncompleteAggregateException.class, u::getJobs);
    }

    @Test
    void shouldReturnUserMinimalByUsername() {
        Optional<User> user = underTest.findMinimalByUsername("existinguser");
        assertThat(user.isPresent()).isTrue();
        var u = user.get();
        assertThat(u.getId()).isEqualTo(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThrows(IncompleteAggregateException.class, u::getJobs);
    }

    @Test
    void shouldReturnUserViews() {
        List<UserView> userViews = underTest.findView(
                DomainSpecification.applySort(
                        DomainSpecification.in("id",
                                List.of(
                                        new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")),
                                        new UserId(UUID.fromString("abcd4321-4321-4321-4321-123456789012"))
                                )
                        ),
                        DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
                )
        );

        assertThat(userViews).hasSize(2);
        assertThat(userViews.getFirst().getEmail()).isEqualTo("existing@example.com");
        assertThat(userViews.get(1).getEmail()).isEqualTo("otherexisting@example.com");
    }

    @Test
    void shouldReturnUserById() {
        Optional<User> user = underTest.findById(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThat(user).get().hasFieldOrPropertyWithValue("email", "existing@example.com");
    }

    @Test
    void shouldReturnUserByEmailOfUsername() {
        Optional<User> user = underTest.findByEmailOrUsername("existing@example.com", "nonexistentUsername");
        assertThat(user.get().getId()).isEqualTo(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));

        Optional<User> user2 = underTest.findByEmailOrUsername("nonexistentEmail@example.com", "existinguser");
        assertThat(user2.get().getId()).isEqualTo(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
    }

    @Test
    void shouldSaveAllUsers() {
        // get 2 known users
        UserId userId1 = new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012"));
        UserId userId2 = new UserId(UUID.fromString("abcd4321-4321-4321-4321-123456789012"));

        DomainSpecification spec = DomainSpecification.applySort(
                DomainSpecification.in("id", List.of(userId1, userId2)),
                DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
        );

        Map<UserId, User> users = underTest.findMinimal(spec);
        assertThat(users).hasSize(2);

        String stringDate = "09:15:30, 10/05/2025";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd/MM/yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, formatter);
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant newInstant = zonedDateTime.toInstant();

        // update jobFollowUpReminderSentAt for both
        Set<User> usersToSave = new HashSet<>();
        usersToSave.add(User.fromMinimal(users.get(userId1)).lastName("new last name 1").jobFollowUpReminderSentAt(newInstant).build());
        usersToSave.add(User.fromMinimal(users.get(userId2)).lastName("new last name 2").jobFollowUpReminderSentAt(newInstant).build());

        // when
        BulkDataSaveResult result = underTest.saveAll(usersToSave);

        assertThat(result).isNotNull();
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.updatedCount()).isEqualTo(2);

        // check that only jobFollowUpReminderSentAt has been saved, as it is the only property supported for bulk updates
        Map<UserId, User> usersReloaded = underTest.findMinimal(spec);
        assertThat(usersReloaded).hasSize(2);
        assertThat(usersReloaded.get(userId1).getLastName()).isEqualTo("User");
        assertThat(usersReloaded.get(userId1).getJobFollowUpReminderSentAt()).isEqualTo(newInstant);
        assertThat(usersReloaded.get(userId2).getLastName()).isEqualTo("OtherUser");
        assertThat(usersReloaded.get(userId2).getJobFollowUpReminderSentAt()).isEqualTo(newInstant);


        // reset original users to ensure tests further consistency and predictability
        // (which in itself could be considered another similar saveAll test)
        // actually we just have to save the users loaded at the beginning of this test
        // because they remain at their original state, as all the other methods worked on copies
        Set<User> usersToReset = new HashSet<>();
        usersToReset.add(users.get(userId1));
        usersToReset.add(users.get(userId2));

        BulkDataSaveResult result2 = underTest.saveAll(usersToReset);

        assertThat(result2).isNotNull();
        assertThat(result2.totalCount()).isEqualTo(2);
        assertThat(result2.updatedCount()).isEqualTo(2);
    }

    @Test
    void whenUsersSetEmpty_thenSaveShouldThrowException() {
        // given
        var emptySet = Collections.<User>emptySet();

        // when + then
        assertThrows(IllegalArgumentException.class, () -> underTest.saveAll(emptySet));
    }

    @Test
    void shouldGetJobStatuses() {
        UserId userId = new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012"));
        Optional<User> user = underTest.findMinimalById(userId);
        assertThat(user).isPresent();

        List< JobStatus> statuses = underTest.getJobsStatuses(user.get());

        assertThat(statuses).hasSize(3);
        assertThat(statuses).containsExactly(JobStatus.CREATED, JobStatus.PENDING, JobStatus.CREATED);
    }
}
