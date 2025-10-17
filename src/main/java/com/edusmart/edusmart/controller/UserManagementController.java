package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.Role;
import com.edusmart.edusmart.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users") // Admin-only access for user management
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // All methods in this controller require ADMIN role
public class UserManagementController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userProfileService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable UUID userId, @RequestParam Role newRole) {
        return ResponseEntity.ok(userProfileService.updateUserRole(userId, newRole));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userProfileService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}