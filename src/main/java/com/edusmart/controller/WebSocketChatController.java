package com.edusmart.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket Controller for Real-Time Chat and Collaboration
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Handle discussion messages
     */
    @MessageMapping("/discussion/{discussionId}")
    @SendTo("/topic/discussion/{discussionId}")
    public ChatMessage sendDiscussionMessage(
            @DestinationVariable Long discussionId,
            @Payload ChatMessage message) {
        log.info("New message in discussion {}: {}", discussionId, message.getContent());
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    /**
     * Handle course chat messages
     */
    @MessageMapping("/course/{courseId}/chat")
    @SendTo("/topic/course/{courseId}/chat")
    public ChatMessage sendCourseMessage(
            @DestinationVariable Long courseId,
            @Payload ChatMessage message) {
        log.info("New message in course {}: {}", courseId, message.getContent());
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    /**
     * Handle private messages between users
     */
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload PrivateMessage message) {
        log.info("Private message from {} to {}", message.getSenderId(), message.getRecipientId());
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(message.getSenderId());
        chatMessage.setSenderName(message.getSenderName());
        chatMessage.setContent(message.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        
        messagingTemplate.convertAndSendToUser(
            message.getRecipientId().toString(),
            "/queue/messages",
            chatMessage
        );
    }
    
    /**
     * User joined notification
     */
    @MessageMapping("/discussion/{discussionId}/join")
    @SendTo("/topic/discussion/{discussionId}/presence")
    public PresenceMessage userJoined(
            @DestinationVariable Long discussionId,
            @Payload PresenceMessage presence) {
        log.info("User {} joined discussion {}", presence.getUserName(), discussionId);
        presence.setAction("JOIN");
        presence.setTimestamp(LocalDateTime.now());
        return presence;
    }
    
    /**
     * User left notification
     */
    @MessageMapping("/discussion/{discussionId}/leave")
    @SendTo("/topic/discussion/{discussionId}/presence")
    public PresenceMessage userLeft(
            @DestinationVariable Long discussionId,
            @Payload PresenceMessage presence) {
        log.info("User {} left discussion {}", presence.getUserName(), discussionId);
        presence.setAction("LEAVE");
        presence.setTimestamp(LocalDateTime.now());
        return presence;
    }
    
    /**
     * Typing indicator
     */
    @MessageMapping("/discussion/{discussionId}/typing")
    public void userTyping(
            @DestinationVariable Long discussionId,
            @Payload TypingIndicator indicator) {
        log.debug("User {} typing in discussion {}", indicator.getUserName(), discussionId);
        messagingTemplate.convertAndSend(
            "/topic/discussion/" + discussionId + "/typing",
            indicator
        );
    }
    
    // DTO Classes
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private Long senderId;
        private String senderName;
        private String content;
        private String messageType; // TEXT, IMAGE, FILE
        private LocalDateTime timestamp;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateMessage {
        private Long senderId;
        private String senderName;
        private Long recipientId;
        private String content;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresenceMessage {
        private Long userId;
        private String userName;
        private String action; // JOIN, LEAVE
        private LocalDateTime timestamp;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypingIndicator {
        private Long userId;
        private String userName;
        private boolean isTyping;
    }
}
