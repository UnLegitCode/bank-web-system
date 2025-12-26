package ru.unlegit.bank.config;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Setter
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinIOConfig {

    @Value("${minio.url}")
    String url;
    @Value("${minio.access_key}")
    String accessKey;
    @Value("${minio.secret_key}")
    String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(url))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.EU_WEST_1)
                .forcePathStyle(true)
                .build();
    }
}