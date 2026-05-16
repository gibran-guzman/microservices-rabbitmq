package com.example.notificationservice.repository;

import com.example.notificationservice.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByOrderId(String orderId);
}