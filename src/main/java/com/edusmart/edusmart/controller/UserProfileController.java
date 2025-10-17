package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable UUID userId) {
        User user = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserProfile(
            @PathVariable UUID userId,
            @RequestBody User updatedUser,
            @AuthenticationPrincipal UserDetails userDetails // To ensure the authenticated user is updating their own profile
    ) {
        // In a real application, you would add authorization logic here
        // to ensure the authenticated user is allowed to update this profile.
        if (!userId.equals(((User) userDetails).getId())) throw new org.springframework.security.access.AccessDeniedException("You are not allowed to update this profile.");

        User user = userProfileService.updateUserProfile(userId, updatedUser);
        return ResponseEntity.ok(user);
    }
}