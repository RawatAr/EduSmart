package com.edusmart.service;

import com.edusmart.dto.certificate.CertificateResponseDTO;
import com.edusmart.entity.Certificate;
import com.edusmart.entity.Course;
import com.edusmart.entity.Enrollment;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.EnrollmentStatus;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.CertificateRepository;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for certificate generation and management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CertificateService {
    
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    /**
     * Generate certificate for completed course
     */
    public CertificateResponseDTO generateCertificate(Long courseId, String username) {
        log.info("Generating certificate for course: {} and user: {}", courseId, username);
        
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        // Check if enrollment exists and is completed
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new BadRequestException("Enrollment not found"));
        
        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new BadRequestException("Course must be completed to generate certificate");
        }
        
        // Check if certificate already exists
        if (certificateRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            return getCertificate(courseId, username);
        }
        
        // Generate certificate number and verification code
        String certificateNumber = generateCertificateNumber();
        String verificationCode = generateVerificationCode();
        
        Certificate certificate = Certificate.builder()
                .student(student)
                .course(course)
                .certificateNumber(certificateNumber)
                .issuedDate(LocalDateTime.now())
                .completionDate(enrollment.getCompletedAt())
                .finalGrade(calculateFinalGrade(student.getId(), courseId))
                .verificationCode(verificationCode)
                .build();
        
        certificate = certificateRepository.save(certificate);
        
        log.info("Certificate generated: {}", certificateNumber);
        
        return mapToDTO(certificate);
    }
    
    /**
     * Get certificate for a course
     */
    @Transactional(readOnly = true)
    public CertificateResponseDTO getCertificate(Long courseId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Certificate certificate = certificateRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        return mapToDTO(certificate);
    }
    
    /**
     * Get all certificates for a student
     */
    @Transactional(readOnly = true)
    public List<CertificateResponseDTO> getMyCertificates(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return certificateRepository.findByStudentIdOrderByIssuedDateDesc(student.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Verify certificate by verification code
     */
    @Transactional(readOnly = true)
    public CertificateResponseDTO verifyCertificate(String verificationCode) {
        Certificate certificate = certificateRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        return mapToDTO(certificate);
    }
    
    /**
     * Generate unique certificate number
     */
    private String generateCertificateNumber() {
        return "CERT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Generate verification code
     */
    private String generateVerificationCode() {
        return UUID.randomUUID().toString().toUpperCase();
    }
    
    /**
     * Calculate final grade (placeholder - can be enhanced)
     */
    private Double calculateFinalGrade(Long studentId, Long courseId) {
        // TODO: Implement actual grade calculation based on assessments
        return 85.0; // Placeholder
    }
    
    /**
     * Map entity to DTO
     */
    private CertificateResponseDTO mapToDTO(Certificate certificate) {
        return CertificateResponseDTO.builder()
                .id(certificate.getId())
                .studentId(certificate.getStudent().getId())
                .studentName(certificate.getStudent().getFirstName() + " " + certificate.getStudent().getLastName())
                .courseId(certificate.getCourse().getId())
                .courseTitle(certificate.getCourse().getTitle())
                .certificateNumber(certificate.getCertificateNumber())
                .issuedDate(certificate.getIssuedDate())
                .completionDate(certificate.getCompletionDate())
                .finalGrade(certificate.getFinalGrade())
                .certificateUrl(certificate.getCertificateUrl())
                .verificationCode(certificate.getVerificationCode())
                .build();
    }
}
