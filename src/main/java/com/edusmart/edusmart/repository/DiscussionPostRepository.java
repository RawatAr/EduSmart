package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, UUID> {
    List<DiscussionPost> findByForumIdOrderByCreatedAtDesc(UUID forumId);
    List<DiscussionPost> findByUserIdOrderByCreatedAtDesc(UUID userId);
}