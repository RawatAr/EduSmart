package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, UUID> {
    List<DiscussionComment> findByPostIdOrderByCreatedAtAsc(UUID postId);
    List<DiscussionComment> findByUserIdOrderByCreatedAtDesc(UUID userId);
}