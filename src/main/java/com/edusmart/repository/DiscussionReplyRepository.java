package com.edusmart.repository;

import com.edusmart.entity.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for DiscussionReply entity
 */
@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {
    
    List<DiscussionReply> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
    
    List<DiscussionReply> findByUserId(Long userId);
    
    @Query("SELECT COUNT(dr) FROM DiscussionReply dr WHERE dr.discussion.id = :discussionId")
    long countByDiscussionId(Long discussionId);
}
