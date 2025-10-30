package com.edusmart.service;

import com.edusmart.dto.user.UpdatePasswordRequest;
import com.edusmart.dto.user.UpdateProfileRequest;
import com.edusmart.dto.user.UserDTO;
import com.edusmart.entity.User;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Get user profile by username
     */
    public UserDTO getUserProfile(String username) {
        log.info("Getting profile for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToDTO(user);
    }
    
    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long userId) {
        log.info("Getting user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToDTO(user);
    }
    
    /**
     * Update user profile
     */
    public UserDTO updateProfile(String username, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            final Long currentUserId = user.getId();
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(currentUserId)) {
                            throw new BadRequestException("Email already in use");
                        }
                    });
            user.setEmail(request.getEmail());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        
        user = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);
        
        return mapToDTO(user);
    }
    
    /**
     * Update password
     */
    public void updatePassword(String username, UpdatePasswordRequest request) {
        log.info("Updating password for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password updated successfully for user: {}", username);
    }
    
    /**
     * Map User entity to UserDTO
     */
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .emailVerified(user.getEmailVerified())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
