package mindaz.simplehtmxcrudauth.shared.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    private String eventType; // LOGIN, PAGE_VIEW, CART_ADDED, LOGOUT
    private Long userId;
    private String userEmail;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}

