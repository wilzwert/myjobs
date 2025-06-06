package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.core.application.usecase.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.DefaultJobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.HtmlJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.JsonLdJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.HtmlFetcherService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.JsHtmlFetcher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.StaticHtmlFetcher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.ExtractJobMetadataUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.model.job.service.JobMetadataService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
import com.wilzwert.myjobs.infrastructure.adapter.fetcher.CustomHtmlFetcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wilhelm Zwertvaegher
 */

@Configuration
public class BeanConfiguration {

    @Bean
    RegisterUseCase registerUseCase(UserDataManager userDataManager, PasswordHasher passwordHasher, AccountCreationMessageProvider messageProvider) {
        return new RegisterUseCaseImpl(userDataManager, passwordHasher, messageProvider);
    }

    @Bean
    LoginUseCase loginUseCase(UserDataManager userDataManager, PasswordHasher passwordHasher, Authenticator authenticator) {
        return new LoginUseCaseImpl(userDataManager, passwordHasher, authenticator);
    }

    @Bean
    DeleteAccountUseCase deleteAccountUseCase(UserDataManager userDataManager, FileStorage fileStorage) {
        return new DeleteAccountUseCaseImpl(userDataManager, fileStorage);
    }

    @Bean
    JobUseCaseImpl jobUseCase(TransactionProvider transactionProvider, IntegrationEventPublisher integrationEventPublisher, JobDataManager jobDataManager, UserDataManager userDataManager, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        return new JobUseCaseImpl(transactionProvider, integrationEventPublisher, jobDataManager, userDataManager, fileStorage, htmlSanitizer);
    }

    @Bean
    PasswordUseCaseImpl passwordUseCase(UserDataManager userDataManager, PasswordResetMessageProvider passwordResetMessageProvider, PasswordHasher passwordHasher) {
        return new PasswordUseCaseImpl(userDataManager, passwordResetMessageProvider, passwordHasher);
    }

    @Bean
    UserUseCaseImpl userUseCase(UserDataManager userDataManager, EmailVerificationMessageProvider emailVerificationMessageProvider) {
        return new UserUseCaseImpl(userDataManager, emailVerificationMessageProvider);
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
    SendJobsRemindersUseCase sendJobsRemindersUseCase(JobDataManager jobDataManager, UserDataManager userDataManager, JobReminderMessageProvider messageProvider) {
        return new SendJobsRemindersUseCaseImpl(jobDataManager, userDataManager, messageProvider);
    }
}
