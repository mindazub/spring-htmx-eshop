package mindaz.simplehtmxcrudauth.infrastructure.security;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.domain.user.service.UserService;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom authentication success handler to:
 * 1. Update user's last login timestamp
 * 2. Log the login event for audit trail
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AuditService auditService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();

            // Update last login timestamp
            userService.updateLastLogin(user);

            // Log the successful login event
            auditService.logEvent(AuditEvent.builder()
                    .eventType("LOGIN")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .details("User logged in successfully")
                    .build());
        }

        // Redirect to products page with a visible success notification
        response.sendRedirect("/products?login=success");
    }
}

