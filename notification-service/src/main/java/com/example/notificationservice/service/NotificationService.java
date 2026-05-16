package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de procesamiento de notificaciones.
 *
 * Maneja la persistencia de registros de notificaciones en la base de datos.
 * Estado inicial siempre es 'SENT' (simulado).
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Constructor con dependencias.
     *
     * @param notificationRepository Repositorio de notificaciones
     */
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Procesa y persiste una notificación.
     *
     * @param eventId Identificador del evento (no puede ser null o vacío)
     * @param orderId ID del pedido relacionado (no puede ser null o vacío)
     * @param email Email del destinatario (no puede ser null o vacío)
     * @return NotificationResponse con los datos persistidos
     * @throws IllegalArgumentException si algún parámetro es null o vacío
     *
     * @example
     * <pre>
     * NotificationResponse response = notificationService.processNotification(
     *     "evt-001", "123", "cliente@test.com"
     * );
     * </pre>
     */
    public NotificationResponse processNotification(String eventId, String orderId, String email) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId es obligatorio");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId es obligatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email es obligatorio");
        }

        NotificationLog notification = new NotificationLog();
        notification.setEventId(eventId);
        notification.setOrderId(orderId);
        notification.setEmail(email);
        notification.setStatus("SENT");

        NotificationLog saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    /**
     * Obtiene todas las notificaciones registradas.
     *
     * @return Lista de NotificationResponse con todas las notificaciones
     *
     * @example
     * <pre>
     * List<NotificationResponse> all = notificationService.getAllNotifications();
     * </pre>
     */
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}