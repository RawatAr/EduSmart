package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final NotificationService notificationService;

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername()); // Assuming username is UUID string

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Send initial unread count
        try {
            long unreadCount = notificationService.getUnreadNotificationCount(userId);
            emitter.send(SseEmitter.event()
                    .name("unread-count")
                    .data(unreadCount));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        // Set up completion and error handling
        emitter.onCompletion(() -> System.out.println("SSE connection completed for user: " + userId));
        emitter.onError((throwable) -> {
            System.out.println("SSE connection error for user: " + userId + " - " + throwable.getMessage());
            emitter.completeWithError(throwable);
        });
        emitter.onTimeout(() -> {
            System.out.println("SSE connection timeout for user: " + userId);
            emitter.complete();
        });

        return emitter;
    }
}