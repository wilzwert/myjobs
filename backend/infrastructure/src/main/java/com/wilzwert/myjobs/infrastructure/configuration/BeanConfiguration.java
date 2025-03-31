package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.usecase.*;
import com.wilzwert.myjobs.core.domain.ports.driven.*;
import com.wilzwert.myjobs.core.domain.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.infrastructure.adapter.LocalFileStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:46
 */

@Configuration
public class BeanConfiguration {

    @Bean
    FileStorage fileStorage() {
        return new LocalFileStorage();
    }

    @Bean
    RegisterUseCase registerUseCase(UserService userService, PasswordHasher passwordHasher, AccountCreationMessageProvider messageProvider) {
        return new RegisterUseCaseImpl(userService, passwordHasher, messageProvider);
    }

    @Bean
    LoginUseCase loginUseCase(UserService userService, PasswordHasher passwordHasher, Authenticator authenticator) {
        return new LoginUseCaseImpl(userService, passwordHasher, authenticator);
    }

    @Bean
    DeleteAccountUseCase deleteAccountUseCase(UserService userService) {
        return new DeleteAccountUseCaseImpl(userService);
    }

    @Bean
    JobUseCaseImpl jobUseCase(JobService jobService, UserService userService, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        return new JobUseCaseImpl(jobService, userService, fileStorage, htmlSanitizer);
    }

    @Bean
    PasswordUseCaseImpl passwordUseCase(UserService userService, PasswordResetMessageProvider passwordResetMessageProvider, PasswordHasher passwordHasher) {
        return new PasswordUseCaseImpl(userService, passwordResetMessageProvider, passwordHasher);
    }
}
