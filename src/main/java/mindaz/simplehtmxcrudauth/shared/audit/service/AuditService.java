package mindaz.simplehtmxcrudauth.shared.audit.service;

import mindaz.simplehtmxcrudauth.shared.audit.entity.AuditLog;
import mindaz.simplehtmxcrudauth.shared.audit.repository.AuditLogRepository;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void logEvent(AuditEvent event) {
        AuditLog log = AuditLog.builder()
                .eventType(event.getEventType())
                .userId(event.getUserId())
                .userEmail(event.getUserEmail())
                .details(event.getDetails())
                .timestamp(LocalDateTime.now())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .build();

        AuditLog saved = auditLogRepository.save(log);

        // Broadcast to WebSocket topic
        messagingTemplate.convertAndSend("/topic/audit-log", saved);
    }

    public List<AuditLog> getLatestAuditLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    public List<AuditLog> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getAuditLogsByEventType(String eventType) {
        return auditLogRepository.findByEventType(eventType);
    }

    public long countUserEvents(Long userId, String eventType) {
        return auditLogRepository.countByUserIdAndEventType(userId, eventType);
    }

    public List<AuditLog> getLatestUserEvents(Long userId, String eventType) {
        return auditLogRepository.findTop5ByUserIdAndEventTypeOrderByTimestampDesc(userId, eventType);
    }
}

