package com.edusmart.controller;

import com.edusmart.dto.file.FileUploadResponseDTO;
import com.edusmart.entity.FileUpload;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for file operations
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileStorageService fileStorageService;
    
    /**
     * Upload file
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FileUploadResponseDTO response = fileStorageService.uploadFile(file, currentUser.getId(), category);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Upload course material
     */
    @PostMapping("/upload/course-material")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponseDTO> uploadCourseMaterial(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FileUploadResponseDTO response = fileStorageService.uploadFile(file, currentUser.getId(), "course_material");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Upload course thumbnail
     */
    @PostMapping("/upload/course-thumbnail")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponseDTO> uploadCourseThumbnail(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FileUploadResponseDTO response = fileStorageService.uploadFile(file, currentUser.getId(), "course_thumbnail");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Upload video
     */
    @PostMapping("/upload/video")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponseDTO> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FileUploadResponseDTO response = fileStorageService.uploadFile(file, currentUser.getId(), "video");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Download file
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        FileUpload fileUpload = fileStorageService.getFileById(fileId);
        Resource resource = fileStorageService.downloadFile(fileId);
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileUpload.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + fileUpload.getOriginalFileName() + "\"")
            .body(resource);
    }
    
    /**
     * View file (inline)
     */
    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long fileId) {
        FileUpload fileUpload = fileStorageService.getFileById(fileId);
        Resource resource = fileStorageService.downloadFile(fileId);
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileUpload.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "inline; filename=\"" + fileUpload.getOriginalFileName() + "\"")
            .body(resource);
    }
    
    /**
     * Delete file
     */
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}
