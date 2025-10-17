package com.edusmart.edusmart.service;

import com.edusmart.edusmart.dto.CourseMaterialDTO;
import com.edusmart.edusmart.model.CourseMaterial;
import com.edusmart.edusmart.model.Lesson;
import com.edusmart.edusmart.repository.CourseMaterialRepository;
import com.edusmart.edusmart.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;
    private final LessonRepository lessonRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<CourseMaterialDTO> getAllCourseMaterials() {
        return courseMaterialRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseMaterialDTO> getCourseMaterialById(UUID id) {
        return courseMaterialRepository.findById(id).map(this::convertToDTO);
    }

    public CourseMaterialDTO createCourseMaterial(CourseMaterial courseMaterial, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));
        courseMaterial.setLesson(lesson);
        courseMaterial.setUploadedAt(LocalDateTime.now());
        CourseMaterial savedMaterial = courseMaterialRepository.save(courseMaterial);

        simpMessagingTemplate.convertAndSend("/topic/notifications", "New course material added: " + savedMaterial.getFileName());

        return convertToDTO(savedMaterial);
    }

    public CourseMaterialDTO updateCourseMaterial(UUID id, CourseMaterial updatedCourseMaterial, UUID lessonId) {
        return courseMaterialRepository.findById(id)
                .map(courseMaterial -> {
                    Lesson lesson = lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));
                    courseMaterial.setFileName(updatedCourseMaterial.getFileName());
                    courseMaterial.setFileType(updatedCourseMaterial.getFileType());
                    courseMaterial.setFilePath(updatedCourseMaterial.getFilePath());
                    courseMaterial.setLesson(lesson);
                    CourseMaterial savedMaterial = courseMaterialRepository.save(courseMaterial);
                    return convertToDTO(savedMaterial);
                })
                .orElseThrow(() -> new RuntimeException("Course Material not found with ID: " + id));
    }

    public void deleteCourseMaterial(UUID id) {
        courseMaterialRepository.deleteById(id);
    }

    public List<CourseMaterialDTO> getCourseMaterialsByLessonId(UUID lessonId) {
        return courseMaterialRepository.findByLessonId(lessonId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CourseMaterialDTO convertToDTO(CourseMaterial courseMaterial) {
        return CourseMaterialDTO.builder()
                .id(courseMaterial.getId())
                .fileName(courseMaterial.getFileName())
                .fileType(courseMaterial.getFileType())
                .filePath(courseMaterial.getFilePath())
                .lessonId(courseMaterial.getLesson().getId())
                .uploadedAt(courseMaterial.getUploadedAt())
                .build();
    }
}