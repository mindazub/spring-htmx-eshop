package mindaz.simplehtmxcrudauth.shared.audit.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // LOGIN, PAGE_VIEW, CART_ADDED, etc.

    @Column(nullable = false)
    private Long userId;

    private String userEmail;

    private String details; // JSON or text details

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String ipAddress;

    private String userAgent;
}

