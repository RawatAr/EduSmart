package com.edusmart.edusmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterialDTO {

    private UUID id;
    private String fileName;
    private String fileType;
    private String filePath;
    private UUID lessonId;
    private LocalDateTime uploadedAt;
}
