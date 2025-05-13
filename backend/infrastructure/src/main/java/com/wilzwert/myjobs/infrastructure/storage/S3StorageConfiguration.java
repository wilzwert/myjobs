package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:09:18
 */

@Configuration
@Profile({"integration", "staging", "prod"})
public class S3StorageConfiguration {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key-id}")
    private String accessKeyId;

    @Value("${aws.s3.secret-access-key}")
    private String accessKeySecret;

    @Bean
    AwsBasicCredentials awsBasicCredentials() {
        return AwsBasicCredentials.create(accessKeyId, accessKeySecret);
    }

    @Bean
    public S3Client s3Client(AwsBasicCredentials awsBasicCredentials) {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsBasicCredentials awsBasicCredentials) {
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public FileStorage fileStorage(S3Client s3Client, S3Presigner s3Presigner) {
        return new S3FileStorage(s3Client, s3Presigner, bucketName);
    }
}