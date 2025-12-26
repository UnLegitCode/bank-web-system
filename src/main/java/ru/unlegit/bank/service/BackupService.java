package ru.unlegit.bank.service;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.dto.backup.BackupInfo;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BackupService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String POSTGRES_PASSWORD_ENV = "PGPASSWORD";

    final S3Client s3Client;
    @Value("${minio.bucket}") String bucket;
    @Value("${backup.pg-dump-path}") String pgDumpPath;
    @Value("${backup.psql-path}") String pgRestorePath;
    @Value("${spring.datasource.url}") String dbUrl;
    @Value("${spring.datasource.username}") String dbUser;
    @Value("${spring.datasource.password}") String dbPass;

    @PostConstruct
    public void initBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException exception) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }

        saveBackup();
    }

    private String extractDbName() {
        return dbUrl.split("/")[3].split("\\?")[0];
    }

    public void saveBackup() {
        String fileName = "backup_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".sql";
        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), fileName);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pgDumpPath,
                    "-h", "postgres",
                    "-U", dbUser,
                    "-d", extractDbName(),
                    "-F", "c",
                    "-f", tempFile.toString()
            );

            processBuilder.environment().put(POSTGRES_PASSWORD_ENV, dbPass);

            int exitCode = processBuilder.start().waitFor();

            if (exitCode == 0) {
                uploadToMinio(tempFile, fileName);
                log.info("Бэкап создан: {}", fileName);
            } else {
                log.error("Ошибка pg_dump, код: {}", exitCode);
            }
        } catch (Exception exception) {
            log.error("Ошибка бэкапа", exception);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException exception) {
                log.warn("Не удалось удалить временный файл", exception);
            }
        }
    }

    @Scheduled(cron = "${backup.cron}")
    public void performBackup() {
        saveBackup();
    }

    private void uploadToMinio(Path file, String key) {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).contentType("application/octet-stream").build(),
                RequestBody.fromFile(file)
        );
    }

    public List<BackupInfo> listBackups() {
        return s3Client.listObjectsV2Paginator(ListObjectsV2Request.builder().bucket(bucket).build())
                .contents()
                .stream()
                .map(object -> new BackupInfo(object.key(), object.lastModified(), object.size()))
                .sorted(Comparator.comparing(BackupInfo::date).reversed())
                .toList();
    }

    public Path downloadBackup(String key) {
        File tempFile = new File("restore" + "_" + key);

        s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build(),
                ResponseTransformer.toFile(tempFile)
        );

        return tempFile.toPath();
    }

    public void restoreBackup(String key) throws Exception {
        Path tempFile = downloadBackup(key);
        ProcessBuilder processBuilder = new ProcessBuilder(
                pgRestorePath,
                "--clean",
                "-h", "postgres",
                "-U", dbUser,
                "-d", extractDbName(),
                tempFile.toString()
        );

        processBuilder.environment().put(POSTGRES_PASSWORD_ENV, dbPass);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        Files.delete(tempFile);

        if (exitCode != 0) {
            String message;

            try (BufferedReader reader = process.errorReader(StandardCharsets.UTF_8)) {
                message = reader.lines().collect(Collectors.joining("\n"));
            }

            if (message.isEmpty()) {
                try (BufferedReader reader = process.inputReader(StandardCharsets.UTF_8)) {
                    message = reader.lines().collect(Collectors.joining("\n"));
                }
            }

            throw new RuntimeException("Ошибка восстановления, код " + exitCode + ", информация " + message);
        }

        log.info("Бэкап восстановлен: {}", key);
    }
}