package com.edusmart.repository;

import com.edusmart.entity.FileUpload;
import com.edusmart.entity.enums.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FileUpload entity
 */
@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    
    List<FileUpload> findByUploadedById(Long userId);
    
    Page<FileUpload> findByUploadedByIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<FileUpload> findByFileType(FileType fileType);
    
    Page<FileUpload> findByFileTypeOrderByCreatedAtDesc(FileType fileType, Pageable pageable);
}
