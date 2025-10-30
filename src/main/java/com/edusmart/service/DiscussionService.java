package com.edusmart.service;

import com.edusmart.dto.discussion.*;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.Role;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing course discussions and forums
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiscussionService {
    
    private final DiscussionRepository discussionRepository;
    private final DiscussionReplyRepository discussionReplyRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Create a new discussion
     */
    public DiscussionResponseDTO createDiscussion(DiscussionRequestDTO request, String username) {
        log.info("Creating discussion: {} by user: {}", request.getTitle(), username);
        
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        // Check if user has access (enrolled or instructor)
        boolean hasAccess = course.getInstructor().getId().equals(author.getId()) ||
                           author.getRole().equals(Role.ADMIN) ||
                           enrollmentRepository.existsByStudentIdAndCourseId(author.getId(), course.getId());
        
        if (!hasAccess) {
            throw new BadRequestException("You must be enrolled in the course to create discussions");
        }
        
        Discussion discussion = Discussion.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .course(course)
                .user(author)
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .isResolved(false)
                .build();
        
        // Link to lesson if provided
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
            discussion.setLesson(lesson);
        }
        
        discussion = discussionRepository.save(discussion);
        log.info("Discussion created: {}", discussion.getId());
        
        return mapToResponseDTO(discussion, true);
    }
    
    /**
     * Get discussion by ID
     */
    public DiscussionResponseDTO getDiscussion(Long discussionId, String username) {
        log.info("Getting discussion: {} for user: {}", discussionId, username);
        
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check access
        boolean hasAccess = discussion.getCourse().getInstructor().getId().equals(user.getId()) ||
                           user.getRole().equals(Role.ADMIN) ||
                           enrollmentRepository.existsByStudentIdAndCourseId(
                                   user.getId(), discussion.getCourse().getId());
        
        if (!hasAccess) {
            throw new BadRequestException("You don't have access to this discussion");
        }
        
        return mapToResponseDTO(discussion, true);
    }
    
    /**
     * Get discussions for a course
     */
    public Page<DiscussionListDTO> getCourseDiscussions(Long courseId, Pageable pageable) {
        log.info("Getting discussions for course: {}", courseId);
        
        Page<Discussion> discussions = discussionRepository.findByCourseIdOrderByIsPinnedDescCreatedAtDesc(
                courseId, pageable);
        
        return discussions.map(this::mapToListDTO);
    }
    
    /**
     * Add reply to discussion
     */
    public DiscussionReplyDTO addReply(Long discussionId, DiscussionReplyDTO request, String username) {
        log.info("Adding reply to discussion: {} by user: {}", discussionId, username);
        
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check access
        boolean hasAccess = discussion.getCourse().getInstructor().getId().equals(author.getId()) ||
                           author.getRole().equals(Role.ADMIN) ||
                           enrollmentRepository.existsByStudentIdAndCourseId(
                                   author.getId(), discussion.getCourse().getId());
        
        if (!hasAccess) {
            throw new BadRequestException("You must be enrolled to reply");
        }
        
        DiscussionReply reply = DiscussionReply.builder()
                .discussion(discussion)
                .user(author)
                .content(request.getContent())
                .build();
        
        // Handle parent reply for nested discussions
        if (request.getParentReplyId() != null) {
            DiscussionReply parentReply = discussionReplyRepository.findById(request.getParentReplyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent reply not found"));
            reply.setParentReply(parentReply);
        }
        
        reply = discussionReplyRepository.save(reply);
        
        // Update discussion last activity
        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);
        
        log.info("Reply added: {}", reply.getId());
        return mapToReplyDTO(reply);
    }
    
    /**
     * Mark discussion as resolved
     */
    public DiscussionResponseDTO markAsResolved(Long discussionId, String username) {
        log.info("Marking discussion {} as resolved", discussionId);
        
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only author, instructor, or admin can mark as resolved
        boolean canResolve = discussion.getAuthor().getId().equals(user.getId()) ||
                            discussion.getCourse().getInstructor().getId().equals(user.getId()) ||
                            user.getRole().equals(Role.ADMIN);
        
        if (!canResolve) {
            throw new BadRequestException("You don't have permission to resolve this discussion");
        }
        
        discussion.setIsResolved(true);
        discussion = discussionRepository.save(discussion);
        
        return mapToResponseDTO(discussion, false);
    }
    
    /**
     * Pin/Unpin discussion (instructors only)
     */
    public DiscussionResponseDTO togglePin(Long discussionId, String username) {
        log.info("Toggling pin for discussion: {}", discussionId);
        
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only instructor or admin can pin
        boolean canPin = discussion.getCourse().getInstructor().getId().equals(user.getId()) ||
                        user.getRole().equals(Role.ADMIN);
        
        if (!canPin) {
            throw new BadRequestException("Only instructors can pin discussions");
        }
        
        discussion.setIsPinned(!discussion.getIsPinned());
        discussion = discussionRepository.save(discussion);
        
        return mapToResponseDTO(discussion, false);
    }
    
    /**
     * Delete discussion
     */
    public void deleteDiscussion(Long discussionId, String username) {
        log.info("Deleting discussion: {}", discussionId);
        
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only author, instructor, or admin can delete
        boolean canDelete = discussion.getAuthor().getId().equals(user.getId()) ||
                           discussion.getCourse().getInstructor().getId().equals(user.getId()) ||
                           user.getRole().equals(Role.ADMIN);
        
        if (!canDelete) {
            throw new BadRequestException("You don't have permission to delete this discussion");
        }
        
        discussionRepository.delete(discussion);
        log.info("Discussion deleted: {}", discussionId);
    }
    
    // Mapping methods
    private DiscussionResponseDTO mapToResponseDTO(Discussion discussion, boolean includeReplies) {
        DiscussionResponseDTO.DiscussionResponseDTOBuilder builder = DiscussionResponseDTO.builder()
                .id(discussion.getId())
                .title(discussion.getTitle())
                .content(discussion.getContent())
                .authorId(discussion.getAuthor().getId())
                .authorName(discussion.getAuthor().getFirstName() + " " + discussion.getAuthor().getLastName())
                .authorRole(discussion.getAuthor().getRole().name())
                .courseId(discussion.getCourse().getId())
                .courseTitle(discussion.getCourse().getTitle())
                .replyCount(discussion.getReplies() != null ? discussion.getReplies().size() : 0)
                .isPinned(discussion.getIsPinned())
                .isResolved(discussion.getIsResolved())
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt());
        
        if (discussion.getLesson() != null) {
            builder.lessonId(discussion.getLesson().getId())
                   .lessonTitle(discussion.getLesson().getTitle());
        }
        
        if (includeReplies && discussion.getReplies() != null) {
            List<DiscussionReplyDTO> replies = discussion.getReplies().stream()
                    .map(this::mapToReplyDTO)
                    .collect(Collectors.toList());
            builder.replies(replies);
        }
        
        return builder.build();
    }
    
    private DiscussionListDTO mapToListDTO(Discussion discussion) {
        String excerpt = discussion.getContent().length() > 150
                ? discussion.getContent().substring(0, 150) + "..."
                : discussion.getContent();
        
        LocalDateTime lastActivity = discussion.getReplies() != null && !discussion.getReplies().isEmpty()
                ? discussion.getReplies().stream()
                    .map(DiscussionReply::getCreatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(discussion.getCreatedAt())
                : discussion.getCreatedAt();
        
        return DiscussionListDTO.builder()
                .id(discussion.getId())
                .title(discussion.getTitle())
                .excerpt(excerpt)
                .authorId(discussion.getAuthor().getId())
                .authorName(discussion.getAuthor().getFirstName() + " " + discussion.getAuthor().getLastName())
                .courseId(discussion.getCourse().getId())
                .courseTitle(discussion.getCourse().getTitle())
                .replyCount(discussion.getReplies() != null ? discussion.getReplies().size() : 0)
                .isPinned(discussion.getIsPinned())
                .isResolved(discussion.getIsResolved())
                .createdAt(discussion.getCreatedAt())
                .lastActivityAt(lastActivity)
                .build();
    }
    
    private DiscussionReplyDTO mapToReplyDTO(DiscussionReply reply) {
        return DiscussionReplyDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .authorId(reply.getAuthor().getId())
                .authorName(reply.getAuthor().getFirstName() + " " + reply.getAuthor().getLastName())
                .authorRole(reply.getAuthor().getRole().name())
                .discussionId(reply.getDiscussion().getId())
                .parentReplyId(reply.getParentReply() != null ? reply.getParentReply().getId() : null)
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}
