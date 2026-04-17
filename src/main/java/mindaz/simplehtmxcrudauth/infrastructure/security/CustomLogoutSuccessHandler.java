package mindaz.simplehtmxcrudauth.infrastructure.security;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom logout handler to audit logout events
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuditService auditService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                               Authentication authentication) throws IOException {
        // Log logout event if user exists
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            auditService.logEvent(AuditEvent.builder()
                    .eventType("LOGOUT")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .details("User logged out successfully")
                    .build());
        }

        // Redirect to login page with a visible logout notification
        response.sendRedirect("/login?logout=success");
    }
}

