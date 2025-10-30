package com.edusmart.entity;

import com.edusmart.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

/**
 * File upload entity for course materials
 */
@Entity
@Table(name = "file_uploads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileUpload extends BaseEntity {
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private Long fileSize; // in bytes
    
    @Column(nullable = false)
    private String contentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
}
