package com.edusmart.repository;

import com.edusmart.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Certificate entity
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    Optional<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    
    Optional<Certificate> findByVerificationCode(String verificationCode);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Certificate> findByStudentIdOrderByIssuedDateDesc(Long studentId);
    
    List<Certificate> findByCourseId(Long courseId);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.student.id = :studentId")
    long countByStudentId(Long studentId);
}
