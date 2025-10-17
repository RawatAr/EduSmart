package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.DiscussionForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionForumRepository extends JpaRepository<DiscussionForum, UUID> {
    List<DiscussionForum> findByCourseId(UUID courseId);
}