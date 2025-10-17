package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.dto.CourseMaterialDTO;
import com.edusmart.edusmart.model.CourseMaterial;
import com.edusmart.edusmart.service.CourseMaterialService;
import com.edusmart.edusmart.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-materials")
@RequiredArgsConstructor
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<CourseMaterialDTO>> getAllCourseMaterials() {
        return ResponseEntity.ok(courseMaterialService.getAllCourseMaterials());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<CourseMaterialDTO> getCourseMaterialById(@PathVariable UUID id) {
        return courseMaterialService.getCourseMaterialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseMaterialDTO> uploadCourseMaterial(
            @RequestParam("file") MultipartFile file,
            @PathVariable UUID lessonId
    ) {
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/course-materials/download/")
                .path(fileName)
                .toUriString();

        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.setFileName(fileName);
        courseMaterial.setFileType(file.getContentType());
        courseMaterial.setFilePath(fileDownloadUri);

        return ResponseEntity.ok(courseMaterialService.createCourseMaterial(courseMaterial, lessonId));
    }

    @GetMapping("/download/{fileName:.+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to the default content type if type could not be determined
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteCourseMaterial(@PathVariable UUID id) {
        courseMaterialService.deleteCourseMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<CourseMaterialDTO>> getCourseMaterialsByLessonId(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(courseMaterialService.getCourseMaterialsByLessonId(lessonId));
    }
}