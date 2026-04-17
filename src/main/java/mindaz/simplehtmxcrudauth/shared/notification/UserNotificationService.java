package mindaz.simplehtmxcrudauth.shared.notification;

import lombok.RequiredArgsConstructor;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.event.UserNotification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notify(User user, String type, String action, String message) {
        UserNotification payload = UserNotification.builder()
                .type(type)
                .action(action)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/notifications", payload);
    }
}

