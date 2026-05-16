package com.example.notificationservice;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void getNotifications_empty_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createNotification_savesToDatabase() throws Exception {
        mockMvc.perform(post("/notifications")
                        .param("eventId", "EVT-001")
                        .param("orderId", "ORD-001")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("EVT-001"))
                .andExpect(jsonPath("$.orderId").value("ORD-001"))
                .andExpect(jsonPath("$.status").value("SENT"));

        List<NotificationLog> logs = notificationRepository.findAll();
        assertTrue(logs.stream().anyMatch(l -> "EVT-001".equals(l.getEventId())));
    }

    @Test
    void getNotifications_returnsSavedNotifications() throws Exception {
        NotificationLog log = new NotificationLog();
        log.setEventId("EVT-02");
        log.setOrderId("ORD-02");
        log.setEmail("test2@example.com");
        log.setStatus("SENT");
        notificationRepository.save(log);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("EVT-02"));
    }
}