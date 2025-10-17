package com.edusmart.edusmart.service;

import com.edusmart.edusmart.dto.CertificateDTO;
import com.edusmart.edusmart.model.Certificate;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.repository.CertificateRepository;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.EnrollmentRepository;
import com.edusmart.edusmart.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    public CertificateDTO generateCertificate(UUID userId, UUID courseId) {
        // Check if certificate already exists
        if (certificateRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("Certificate already exists for this user and course");
        }

        // Get course and user (simplified - assuming they exist)
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User user = new User(); // Would need proper user lookup
        user.setId(userId);
        user.setFirstName("Student"); // Placeholder
        user.setLastName("User"); // Placeholder

        // Calculate final score (simplified)
        double finalScore = 85.0; // Placeholder
        String grade = calculateGrade(finalScore);

        // Generate certificate number
        String certificateNumber = generateCertificateNumber(userId, courseId);

        // Create certificate entity
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setCertificateNumber(certificateNumber);
        certificate.setIssuedDate(LocalDateTime.now());
        certificate.setFinalScore(finalScore);
        certificate.setGrade(grade);
        certificate.setCompletionDate(LocalDateTime.now().minusDays(1));
        certificate.setCertificateUrl("/certificates/" + certificateNumber + ".pdf");

        Certificate savedCertificate = certificateRepository.save(certificate);

        return convertToDTO(savedCertificate);
    }

    public List<CertificateDTO> getUserCertificates(UUID userId) {
        List<Certificate> certificates = certificateRepository.findByUserId(userId);
        return certificates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CertificateDTO> getCertificate(UUID certificateId) {
        return certificateRepository.findById(certificateId)
                .map(this::convertToDTO);
    }

    public Optional<CertificateDTO> getCertificateByUserAndCourse(UUID userId, UUID courseId) {
        return certificateRepository.findByUserIdAndCourseId(userId, courseId)
                .map(this::convertToDTO);
    }

    public List<CertificateDTO> getCertificatesByCourse(UUID courseId) {
        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);
        return certificates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private double calculateFinalScore(UUID userId, UUID courseId) {
        // Simplified calculation - placeholder implementation
        return 85.0; // Would need proper assessment weighting in real implementation
    }

    private String calculateGrade(double score) {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    private String generateCertificateNumber(UUID userId, UUID courseId) {
        // Generate a unique certificate number
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userPart = userId.toString().substring(0, 8).toUpperCase();
        String coursePart = courseId.toString().substring(0, 8).toUpperCase();
        return "CERT-" + timestamp + "-" + userPart + "-" + coursePart;
    }

    private CertificateDTO convertToDTO(Certificate certificate) {
        return CertificateDTO.builder()
                .id(certificate.getId())
                .userId(certificate.getUser().getId())
                .userName(certificate.getUser().getFirstName() + " " + certificate.getUser().getLastName())
                .courseId(certificate.getCourse().getId())
                .courseTitle(certificate.getCourse().getTitle())
                .certificateNumber(certificate.getCertificateNumber())
                .issuedDate(certificate.getIssuedDate())
                .finalScore(certificate.getFinalScore())
                .grade(certificate.getGrade())
                .certificateUrl(certificate.getCertificateUrl())
                .instructorName(certificate.getCourse().getInstructor().getFirstName() + " " +
                               certificate.getCourse().getInstructor().getLastName())
                .completionDate(certificate.getCompletionDate())
                .build();
    }
}