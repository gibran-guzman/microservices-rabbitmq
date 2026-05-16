package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestParam("eventId") String eventId,
            @RequestParam("orderId") String orderId,
            @RequestParam("email") String email) {
        NotificationResponse response = notificationService.processNotification(eventId, orderId, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}