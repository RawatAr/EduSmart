package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.DiscussionComment;
import com.edusmart.edusmart.model.DiscussionForum;
import com.edusmart.edusmart.model.DiscussionPost;
import com.edusmart.edusmart.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    // Forum endpoints
    @GetMapping("/forums")
    public ResponseEntity<List<DiscussionForum>> getAllForums() {
        return ResponseEntity.ok(discussionService.getAllForums());
    }

    @GetMapping("/forums/{forumId}")
    public ResponseEntity<DiscussionForum> getForumById(@PathVariable UUID forumId) {
        return ResponseEntity.ok(discussionService.getForumById(forumId));
    }

    @GetMapping("/courses/{courseId}/forums")
    public ResponseEntity<List<DiscussionForum>> getForumsByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(discussionService.getForumsByCourseId(courseId));
    }

    @PostMapping("/forums")
    public ResponseEntity<DiscussionForum> createForum(@RequestBody DiscussionForum forum) {
        return ResponseEntity.ok(discussionService.createForum(forum));
    }

    @DeleteMapping("/forums/{forumId}")
    public ResponseEntity<Void> deleteForum(@PathVariable UUID forumId) {
        discussionService.deleteForum(forumId);
        return ResponseEntity.noContent().build();
    }

    // Post endpoints
    @GetMapping("/forums/{forumId}/posts")
    public ResponseEntity<List<DiscussionPost>> getPostsByForumId(@PathVariable UUID forumId) {
        return ResponseEntity.ok(discussionService.getPostsByForumId(forumId));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<DiscussionPost> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(discussionService.getPostById(postId));
    }

    @PostMapping("/forums/{forumId}/posts")
    public ResponseEntity<DiscussionPost> createPost(
            @RequestBody DiscussionPost post,
            @PathVariable UUID forumId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(discussionService.createPost(post, userId, forumId));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<DiscussionPost> updatePost(
            @PathVariable UUID postId,
            @RequestBody DiscussionPost post) {
        return ResponseEntity.ok(discussionService.updatePost(postId, post));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        discussionService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // Comment endpoints
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<DiscussionComment>> getCommentsByPostId(@PathVariable UUID postId) {
        return ResponseEntity.ok(discussionService.getCommentsByPostId(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<DiscussionComment> createComment(
            @RequestBody DiscussionComment comment,
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(discussionService.createComment(comment, userId, postId));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<DiscussionComment> updateComment(
            @PathVariable UUID commentId,
            @RequestBody DiscussionComment comment) {
        return ResponseEntity.ok(discussionService.updateComment(commentId, comment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        discussionService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Presence endpoints
    @PostMapping("/forums/{forumId}/join")
    public ResponseEntity<Void> joinForum(
            @PathVariable UUID forumId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        discussionService.userJoinedForum(userId, forumId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forums/{forumId}/leave")
    public ResponseEntity<Void> leaveForum(
            @PathVariable UUID forumId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        discussionService.userLeftForum(userId, forumId);
        return ResponseEntity.ok().build();
    }
}