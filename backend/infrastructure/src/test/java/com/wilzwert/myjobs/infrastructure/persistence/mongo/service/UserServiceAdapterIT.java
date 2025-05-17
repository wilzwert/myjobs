package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.exception.IncompleteAggregateException;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:14:10
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class UserServiceAdapterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private UserServiceAdapter underTest;

    @Test
    public void shouldReturnAllsUsersMinimalByEmail() {
        Map<UserId, User> users = underTest.findMinimal(DomainSpecification.In("email", List.of("existing@example.com", "otherexisting@example.com")));
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.values().stream().toList().getFirst().getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().getFirst().getJobs());
        assertThat(users.values().stream().toList().get(1).getEmail()).isEqualTo("otherexisting@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().get(1).getJobs());
    }

    @Test
    public void shouldReturnAllsUsersMinimalById() {
        Map<UserId, User> users = underTest.findMinimal(
                DomainSpecification.applySort(
                    DomainSpecification.In("id",
                            List.of(
                                    new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")),
                                    new UserId(UUID.fromString("abcd4321-4321-4321-4321-123456789012"))
                            )
                    ),
                    DomainSpecification.Sort("id", DomainSpecification.SortDirection.ASC)
                )
        );
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.values().stream().toList().getFirst().getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().getFirst().getJobs());
        assertThat(users.values().stream().toList().get(1).getEmail()).isEqualTo("otherexisting@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().get(1).getJobs());
    }

    @Test
    public void shouldReturnUserById() {
        Optional<User> user = underTest.findById(new UserId(UUID.fromString("abcd1234-1234-1234-1234-123456789012")));
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("existing@example.com");
    }
}
