package mindaz.simplehtmxcrudauth.shared.event;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class UserNotification {
    String type;
    String action;
    String message;
    LocalDateTime timestamp;
}

