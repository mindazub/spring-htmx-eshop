package mindaz.simplehtmxcrudauth.shared.audit.repository;

import mindaz.simplehtmxcrudauth.shared.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEventType(String eventType);
    List<AuditLog> findTop100ByOrderByTimestampDesc();
    long countByUserIdAndEventType(Long userId, String eventType);
    List<AuditLog> findTop5ByUserIdAndEventTypeOrderByTimestampDesc(Long userId, String eventType);
}

