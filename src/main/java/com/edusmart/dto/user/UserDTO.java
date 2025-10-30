package com.edusmart.dto.user;

import com.edusmart.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String bio;
    private String profilePicture;
    private Role role;
    private Boolean emailVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
