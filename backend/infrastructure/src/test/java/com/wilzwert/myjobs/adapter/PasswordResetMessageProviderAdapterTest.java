package com.wilzwert.myjobs.adapter;

import com.wilzwert.myjobs.core.domain.model.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.adapter.PasswordResetMessageProviderAdapter;
import jakarta.mail.MessagingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PasswordResetMessageProviderAdapterTest {
    @Autowired
    private PasswordResetMessageProviderAdapter underTest;

    @Test
    public void testSendEmail()  {
        underTest.send(new User(UserId.generate(), "user@example.com", EmailStatus.VALIDATED, "", "password", "user", "John", "Doe", "USER", null, null, Instant.now(), Instant.now(), Collections.emptyList()));
    }


}
