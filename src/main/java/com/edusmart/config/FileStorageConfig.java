package com.edusmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for file storage
 */
@Configuration
@Getter
@Slf4j
public class FileStorageConfig {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.max.size:10485760}") // 10MB default
    private long maxFileSize;

    @Value("${file.allowed.extensions:jpg,jpeg,png,gif,pdf,mp4,webm,zip}")
    private String allowedExtensions;

    @Value("${file.storage.type:local}") // local or s3
    private String storageType;

    // AWS S3 Configuration
    @Value("${aws.s3.bucket:}")
    private String s3Bucket;

    @Value("${aws.s3.region:us-east-1}")
    private String s3Region;

    @Value("${aws.access.key:}")
    private String awsAccessKey;

    @Value("${aws.secret.key:}")
    private String awsSecretKey;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("File upload directory created: {}", uploadPath);
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public boolean isS3Enabled() {
        return "s3".equalsIgnoreCase(storageType) && 
               s3Bucket != null && !s3Bucket.isEmpty();
    }
}
