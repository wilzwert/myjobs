package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.usecase.JobUseCaseImpl;
import com.wilzwert.myjobs.core.domain.ports.driven.*;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.application.usecase.LoginUseCaseImpl;
import com.wilzwert.myjobs.core.application.usecase.RegisterUseCaseImpl;
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
    RegisterUseCase registerUseCase(UserService userService, PasswordHasher passwordHasher) {
        return new RegisterUseCaseImpl(userService, passwordHasher);
    }

    @Bean
    LoginUseCase loginUseCase(UserService userService, PasswordHasher passwordHasher, Authenticator authenticator) {
        return new LoginUseCaseImpl(userService, passwordHasher, authenticator);
    }

    @Bean
    JobUseCaseImpl jobUseCase(JobService jobService, UserService userService) {
        return new JobUseCaseImpl(jobService, userService);
    }
}
