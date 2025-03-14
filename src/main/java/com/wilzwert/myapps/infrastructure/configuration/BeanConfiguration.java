package com.wilzwert.myapps.infrastructure.configuration;


import com.wilzwert.myapps.domain.ports.driven.Authenticator;
import com.wilzwert.myapps.domain.ports.driven.JobRepository;
import com.wilzwert.myapps.domain.ports.driven.PasswordHasher;
import com.wilzwert.myapps.domain.ports.driven.UserRepository;
import com.wilzwert.myapps.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myapps.domain.ports.driving.LoginUseCase;
import com.wilzwert.myapps.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myapps.domain.usecase.CreateJobUseCaseImpl;
import com.wilzwert.myapps.domain.usecase.LoginUseCaseImpl;
import com.wilzwert.myapps.domain.usecase.RegisterUseCaseImpl;
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
