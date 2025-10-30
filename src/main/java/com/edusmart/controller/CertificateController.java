package com.edusmart.controller;

import com.edusmart.dto.certificate.CertificateResponseDTO;
import com.edusmart.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for certificates
 */
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {
    
    private final CertificateService certificateService;
    
    /**
     * Generate certificate for completed course
     */
    @PostMapping("/generate/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificateResponseDTO> generateCertificate(
            @PathVariable Long courseId,
            Authentication authentication) {
        CertificateResponseDTO certificate = certificateService.generateCertificate(courseId, authentication.getName());
        return ResponseEntity.ok(certificate);
    }
    
    /**
     * Get certificate for a course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificateResponseDTO> getCertificate(
            @PathVariable Long courseId,
            Authentication authentication) {
        CertificateResponseDTO certificate = certificateService.getCertificate(courseId, authentication.getName());
        return ResponseEntity.ok(certificate);
    }
    
    /**
     * Get all my certificates
     */
    @GetMapping("/my-certificates")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificateResponseDTO>> getMyCertificates(Authentication authentication) {
        List<CertificateResponseDTO> certificates = certificateService.getMyCertificates(authentication.getName());
        return ResponseEntity.ok(certificates);
    }
    
    /**
     * Verify certificate (public endpoint)
     */
    @GetMapping("/verify/{verificationCode}")
    public ResponseEntity<CertificateResponseDTO> verifyCertificate(@PathVariable String verificationCode) {
        CertificateResponseDTO certificate = certificateService.verifyCertificate(verificationCode);
        return ResponseEntity.ok(certificate);
    }
}
