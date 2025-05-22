package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.usecase.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.DefaultJobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.HtmlJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.JsonLdJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.HtmlFetcherService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.JsHtmlFetcher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.StaticHtmlFetcher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.ExtractJobMetadataUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.model.job.service.JobMetadataService;
import com.wilzwert.myjobs.infrastructure.adapter.*;
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
    RegisterUseCase registerUseCase(UserService userService, PasswordHasher passwordHasher, AccountCreationMessageProvider messageProvider) {
        return new RegisterUseCaseImpl(userService, passwordHasher, messageProvider);
    }

    @Bean
    LoginUseCase loginUseCase(UserService userService, PasswordHasher passwordHasher, Authenticator authenticator) {
        return new LoginUseCaseImpl(userService, passwordHasher, authenticator);
    }

    @Bean
    DeleteAccountUseCase deleteAccountUseCase(UserService userService, JobService jobService, FileStorage fileStorage) {
        return new DeleteAccountUseCaseImpl(userService, jobService, fileStorage);
    }

    @Bean
    JobUseCaseImpl jobUseCase(JobService jobService, UserService userService, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        return new JobUseCaseImpl(jobService, userService, fileStorage, htmlSanitizer);
    }

    @Bean
    PasswordUseCaseImpl passwordUseCase(UserService userService, PasswordResetMessageProvider passwordResetMessageProvider, PasswordHasher passwordHasher) {
        return new PasswordUseCaseImpl(userService, passwordResetMessageProvider, passwordHasher);
    }

    @Bean
    UserUseCaseImpl userUseCase(UserService userService, EmailVerificationMessageProvider emailVerificationMessageProvider) {
        return new UserUseCaseImpl(userService, emailVerificationMessageProvider);
    }

    @Bean
    public HtmlFetcherService htmlFetcher(JsHtmlFetcher jsHtmlFetcher, StaticHtmlFetcher staticHtmlFetcher) {
        return new CustomHtmlFetcherService()
                .with(jsHtmlFetcher)
                .with(staticHtmlFetcher)
                ;
    }

    @Bean
    public JobMetadataExtractorService jobMetadataExtractor() {
        return new DefaultJobMetadataExtractorService()
                // use concrete extractors provided by domain for simplicity
                .with(new JsonLdJobMetadataExtractor())
                .with(new HtmlJobMetadataExtractor())
                ;
    }

    @Bean
    public JobMetadataService jobMetadataExtractorService(HtmlFetcherService htmlFetcherService, JobMetadataExtractorService jobMetadataExtractorService) {
        return new JobMetadataService(htmlFetcherService, jobMetadataExtractorService);
    }

    @Bean
    ExtractJobMetadataUseCase extractJobMetadataUseCase(JobMetadataService jobMetadataService) {
        return new ExtractJobMetadataUseCaseImpl(jobMetadataService);
    }

    @Bean
    SendJobsRemindersUseCase sendJobsRemindersUseCase(JobService jobService, UserService userService, JobReminderMessageProvider messageProvider) {
        return new SendJobsRemindersUseCaseImpl(jobService, userService, messageProvider);
    }
}
