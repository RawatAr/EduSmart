package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.*;
import com.edusmart.edusmart.repository.DiscussionCommentRepository;
import com.edusmart.edusmart.repository.DiscussionForumRepository;
import com.edusmart.edusmart.repository.DiscussionPostRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final DiscussionForumRepository forumRepository;
    private final DiscussionPostRepository postRepository;
    private final DiscussionCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Forum operations
    public List<DiscussionForum> getAllForums() {
        return forumRepository.findAll();
    }

    public DiscussionForum getForumById(UUID forumId) {
        return forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found with ID: " + forumId));
    }

    public List<DiscussionForum> getForumsByCourseId(UUID courseId) {
        return forumRepository.findByCourseId(courseId);
    }

    public DiscussionForum createForum(DiscussionForum forum) {
        return forumRepository.save(forum);
    }

    public void deleteForum(UUID forumId) {
        forumRepository.deleteById(forumId);
    }

    // Post operations
    public List<DiscussionPost> getPostsByForumId(UUID forumId) {
        return postRepository.findByForumIdOrderByCreatedAtDesc(forumId);
    }

    public DiscussionPost getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }

    public DiscussionPost createPost(DiscussionPost post, UUID userId, UUID forumId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        DiscussionForum forum = getForumById(forumId);

        post.setUser(user);
        post.setForum(forum);

        DiscussionPost savedPost = postRepository.save(post);

        // Send real-time notification
        messagingTemplate.convertAndSend("/topic/forum/" + forumId + "/posts",
                savedPost);

        return savedPost;
    }

    public DiscussionPost updatePost(UUID postId, DiscussionPost updatedPost) {
        DiscussionPost existingPost = getPostById(postId);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        return postRepository.save(existingPost);
    }

    public void deletePost(UUID postId) {
        postRepository.deleteById(postId);
    }

    // Comment operations
    public List<DiscussionComment> getCommentsByPostId(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public DiscussionComment createComment(DiscussionComment comment, UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        DiscussionPost post = getPostById(postId);

        comment.setUser(user);
        comment.setPost(post);

        DiscussionComment savedComment = commentRepository.save(comment);

        // Send real-time notification
        messagingTemplate.convertAndSend("/topic/post/" + postId + "/comments",
                savedComment);

        return savedComment;
    }

    public DiscussionComment updateComment(UUID commentId, DiscussionComment updatedComment) {
        DiscussionComment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        existingComment.setContent(updatedComment.getContent());
        return commentRepository.save(existingComment);
    }

    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    // User presence tracking (simplified)
    public void userJoinedForum(UUID userId, UUID forumId) {
        Map<String, Object> presenceData = new HashMap<>();
        presenceData.put("userId", userId);
        presenceData.put("action", "joined");
        messagingTemplate.convertAndSend("/topic/forum/" + forumId + "/presence", presenceData);
    }

    public void userLeftForum(UUID userId, UUID forumId) {
        Map<String, Object> presenceData = new HashMap<>();
        presenceData.put("userId", userId);
        presenceData.put("action", "left");
        messagingTemplate.convertAndSend("/topic/forum/" + forumId + "/presence", presenceData);
    }
}