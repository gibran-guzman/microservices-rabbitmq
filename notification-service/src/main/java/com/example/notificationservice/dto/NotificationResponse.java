package com.example.notificationservice.dto;

import com.example.notificationservice.entity.NotificationLog;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String eventId;
    private String orderId;
    private String email;
    private String status;
    private LocalDateTime processedAt;

    public static NotificationResponse fromEntity(NotificationLog log) {
        NotificationResponse response = new NotificationResponse();
        response.setId(log.getId());
        response.setEventId(log.getEventId());
        response.setOrderId(log.getOrderId());
        response.setEmail(log.getEmail());
        response.setStatus(log.getStatus());
        response.setProcessedAt(log.getProcessedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}