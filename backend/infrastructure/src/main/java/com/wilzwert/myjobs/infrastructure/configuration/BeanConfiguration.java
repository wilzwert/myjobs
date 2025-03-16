package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.JobUseCaseImpl;
import com.wilzwert.myjobs.core.domain.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.application.LoginUseCaseImpl;
import com.wilzwert.myjobs.core.application.RegisterUseCaseImpl;
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
    RegisterUseCase registerUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new RegisterUseCaseImpl(userRepository, passwordHasher);
    }

    @Bean
    LoginUseCase loginUseCase(UserRepository userRepository, PasswordHasher passwordHasher, Authenticator authenticator) {
        return new LoginUseCaseImpl(userRepository, passwordHasher, authenticator);
    }

    @Bean
    JobUseCaseImpl jobUseCase(JobRepository jobRepository, UserRepository userRepository) {
        return new JobUseCaseImpl(jobRepository, userRepository);
    }
}
