package com.edusmart.edusmart.dto;

import com.edusmart.edusmart.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String fullName; // Added for web form
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // Added for web form
    private Role role;
}