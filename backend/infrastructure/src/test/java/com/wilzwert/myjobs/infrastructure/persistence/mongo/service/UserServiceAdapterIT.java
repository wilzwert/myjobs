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
    public void shouldReturnAllsUsersMinimal() {
        Map<UserId, User> users = underTest.findMinimal(DomainSpecification.In("email", List.of("existing@example.com", "otherexisting@example.com")));
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.values().stream().toList().getFirst().getEmail()).isEqualTo("existing@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().getFirst().getJobs());
        assertThat(users.values().stream().toList().get(1).getEmail()).isEqualTo("otherexisting@example.com");
        assertThrows(IncompleteAggregateException.class, () -> users.values().stream().toList().get(1).getJobs());
    }
}
