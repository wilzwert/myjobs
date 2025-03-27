package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.usecase.JobUseCaseImpl;
import com.wilzwert.myjobs.core.domain.ports.driven.*;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.application.usecase.LoginUseCaseImpl;
import com.wilzwert.myjobs.core.application.usecase.RegisterUseCaseImpl;
import com.wilzwert.myjobs.core.domain.services.UserDomainService;
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

    @Bean UserDomainService userDomainService(UserService userService, PasswordHasher passwordHasher) {
        return new UserDomainService(userService, passwordHasher);
    }

    @Bean
    RegisterUseCase registerUseCase(UserDomainService userDomainService, UserService userService) {
        return new RegisterUseCaseImpl(userDomainService, userService);
    }

    @Bean
    LoginUseCase loginUseCase(UserDomainService userDomainService, Authenticator authenticator) {
        return new LoginUseCaseImpl(userDomainService, authenticator);
    }

    @Bean
    JobUseCaseImpl jobUseCase(JobService jobService, UserService userService, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        return new JobUseCaseImpl(jobService, userService, fileStorage, htmlSanitizer);
    }
}
