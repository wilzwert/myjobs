package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.domain.ports.driven.Authenticator;
import com.wilzwert.myjobs.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myjobs.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.domain.usecase.CreateJobUseCaseImpl;
import com.wilzwert.myjobs.domain.usecase.LoginUseCaseImpl;
import com.wilzwert.myjobs.domain.usecase.RegisterUseCaseImpl;
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
    CreateJobUseCase createJobUseCase(JobRepository jobRepository, UserRepository userRepository) {
        return new CreateJobUseCaseImpl(jobRepository, userRepository);
    }
}
