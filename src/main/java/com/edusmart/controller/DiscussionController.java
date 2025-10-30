package com.edusmart.controller;

import com.edusmart.dto.discussion.*;
import com.edusmart.service.DiscussionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for discussion management
 */
@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionController {
    
    private final DiscussionService discussionService;
    
    /**
     * Create a new discussion
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscussionResponseDTO> createDiscussion(
            @Valid @RequestBody DiscussionRequestDTO request,
            Authentication authentication) {
        DiscussionResponseDTO discussion = discussionService.createDiscussion(request, authentication.getName());
        return new ResponseEntity<>(discussion, HttpStatus.CREATED);
    }
    
    /**
     * Get discussion by ID
     */
    @GetMapping("/{discussionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscussionResponseDTO> getDiscussion(
            @PathVariable Long discussionId,
            Authentication authentication) {
        DiscussionResponseDTO discussion = discussionService.getDiscussion(discussionId, authentication.getName());
        return ResponseEntity.ok(discussion);
    }
    
    /**
     * Get discussions for a course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DiscussionListDTO>> getCourseDiscussions(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DiscussionListDTO> discussions = discussionService.getCourseDiscussions(courseId, pageable);
        return ResponseEntity.ok(discussions);
    }
    
    /**
     * Add reply to discussion
     */
    @PostMapping("/{discussionId}/replies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscussionReplyDTO> addReply(
            @PathVariable Long discussionId,
            @Valid @RequestBody DiscussionReplyDTO request,
            Authentication authentication) {
        DiscussionReplyDTO reply = discussionService.addReply(discussionId, request, authentication.getName());
        return ResponseEntity.ok(reply);
    }
    
    /**
     * Mark discussion as resolved
     */
    @PatchMapping("/{discussionId}/resolve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscussionResponseDTO> markAsResolved(
            @PathVariable Long discussionId,
            Authentication authentication) {
        DiscussionResponseDTO discussion = discussionService.markAsResolved(discussionId, authentication.getName());
        return ResponseEntity.ok(discussion);
    }
    
    /**
     * Pin/Unpin discussion
     */
    @PatchMapping("/{discussionId}/pin")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<DiscussionResponseDTO> togglePin(
            @PathVariable Long discussionId,
            Authentication authentication) {
        DiscussionResponseDTO discussion = discussionService.togglePin(discussionId, authentication.getName());
        return ResponseEntity.ok(discussion);
    }
    
    /**
     * Delete discussion
     */
    @DeleteMapping("/{discussionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable Long discussionId,
            Authentication authentication) {
        discussionService.deleteDiscussion(discussionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
