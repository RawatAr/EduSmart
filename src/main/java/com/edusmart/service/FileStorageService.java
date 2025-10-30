package com.edusmart.service;

import com.edusmart.config.FileStorageConfig;
import com.edusmart.dto.file.FileUploadResponseDTO;
import com.edusmart.entity.FileUpload;
import com.edusmart.entity.User;
import com.edusmart.exception.BadRequestException;
import com.edusmart.repository.FileUploadRepository;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * Service for file storage operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;
    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;

    /**
     * Upload file
     */
    public FileUploadResponseDTO uploadFile(MultipartFile file, Long userId, String category) {
        validateFile(file);

        try {
            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // Save file to disk
            Path targetLocation = fileStorageConfig.getUploadPath().resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save file metadata to database
            User user = userRepository.findById(userId).orElse(null);
            
            FileUpload fileUpload = FileUpload.builder()
                .originalFileName(originalFilename)
                .fileName(uniqueFilename)
                .filePath(targetLocation.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .fileType(determineFileType(fileExtension))
                .uploadedBy(user)
                .build();

            fileUpload = fileUploadRepository.save(fileUpload);

            log.info("File uploaded successfully: {}", uniqueFilename);

            return FileUploadResponseDTO.builder()
                .fileId(fileUpload.getId())
                .fileName(originalFilename)
                .fileUrl("/api/files/download/" + fileUpload.getId())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(user != null ? user.getEmail() : "Unknown")
                .uploadedAt(LocalDateTime.now().toString())
                .build();

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Download file
     */
    public Resource downloadFile(Long fileId) {
        try {
            FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new BadRequestException("File not found"));

            Path filePath = Path.of(fileUpload.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BadRequestException("File not found or not readable");
            }
        } catch (Exception e) {
            log.error("Failed to download file", e);
            throw new BadRequestException("Failed to download file: " + e.getMessage());
        }
    }

    /**
     * Delete file
     */
    public void deleteFile(Long fileId) {
        try {
            FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new BadRequestException("File not found"));

            // Delete from disk
            Path filePath = Path.of(fileUpload.getFilePath());
            Files.deleteIfExists(filePath);

            // Delete from database
            fileUploadRepository.delete(fileUpload);

            log.info("File deleted successfully: {}", fileUpload.getFileName());
        } catch (IOException e) {
            log.error("Failed to delete file", e);
            throw new BadRequestException("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        // Check file size
        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + 
                fileStorageConfig.getMaxFileSize() + " bytes");
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        
        String[] allowedExtensions = fileStorageConfig.getAllowedExtensions().split(",");
        boolean isAllowed = Arrays.stream(allowedExtensions)
            .anyMatch(ext -> ext.trim().equalsIgnoreCase(extension));

        if (!isAllowed) {
            throw new BadRequestException("File type '" + extension + "' is not allowed. Allowed types: " + 
                fileStorageConfig.getAllowedExtensions());
        }

        // Check for directory traversal attacks
        if (filename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence");
        }
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
    }

    /**
     * Get file by ID
     */
    public FileUpload getFileById(Long fileId) {
        return fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new BadRequestException("File not found"));
    }

    /**
     * Determine file type from extension
     */
    private com.edusmart.entity.enums.FileType determineFileType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return com.edusmart.entity.enums.FileType.OTHER;
        }
        
        String ext = extension.toLowerCase();
        
        // Images
        if (ext.matches("jpg|jpeg|png|gif|bmp|webp|svg")) {
            return com.edusmart.entity.enums.FileType.IMAGE;
        }
        // Videos
        if (ext.matches("mp4|webm|avi|mov|mkv|flv")) {
            return com.edusmart.entity.enums.FileType.VIDEO;
        }
        // Documents
        if (ext.matches("pdf|doc|docx|ppt|pptx|xls|xlsx|txt")) {
            return com.edusmart.entity.enums.FileType.DOCUMENT;
        }
        // Archives
        if (ext.matches("zip|rar|7z|tar|gz")) {
            return com.edusmart.entity.enums.FileType.OTHER;
        }
        
        return com.edusmart.entity.enums.FileType.OTHER;
    }
}
