package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
// FIXME : useless as it doesn't actually test something
public class PasswordResetMessageProviderAdapterTest {
    @Autowired
    private PasswordResetMessageProviderAdapter underTest;

    @Test
    public void testSendEmail()  {
        User user = User.builder()
            .id(UserId.generate())
            .email("user@example.com")
            .emailStatus(EmailStatus.VALIDATED)
            .password("password")
            .username("user")
            .firstName("John")
            .lastName("Doe")
            .role("USER")
            .build();
        underTest.send(user);
    }
}
