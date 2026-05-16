package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse processNotification(String eventId, String orderId, String email) {
        NotificationLog notification = new NotificationLog();
        notification.setEventId(eventId);
        notification.setOrderId(orderId);
        notification.setEmail(email);
        notification.setStatus("SENT");

        NotificationLog saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}