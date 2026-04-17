package mindaz.simplehtmxcrudauth.infrastructure.security;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * SessionHelper - Provides convenient methods for session management
 * Similar to Laravel's session helper functions
 */
@Component
public class SessionHelper {

    /**
     * Check if user is currently authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String);
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get current user's ID
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Get current user's email
     */
    public String getCurrentUserEmail() {
        User user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Get current user's full name
     */
    public String getCurrentUserFullName() {
        User user = getCurrentUser();
        return user != null ? user.getFullName() : null;
    }

    /**
     * Check if current user has admin role
     */
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRoles().contains(User.UserRole.ADMIN);
    }

    /**
     * Check if current user has manager role
     */
    public boolean isManager() {
        User user = getCurrentUser();
        return user != null && user.getRoles().contains(User.UserRole.MANAGER);
    }

    /**
     * Check if current user is regular user
     */
    public boolean isUser() {
        User user = getCurrentUser();
        return user != null && user.getRoles().contains(User.UserRole.USER);
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(User.UserRole role) {
        User user = getCurrentUser();
        return user != null && user.getRoles().contains(role);
    }

    /**
     * Get session creation time from HttpSession
     */
    public LocalDateTime getSessionCreatedAt(HttpSession session) {
        if (session != null) {
            long creationTime = session.getCreationTime();
            return LocalDateTime.from(java.time.Instant.ofEpochMilli(creationTime)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        return null;
    }

    /**
     * Get session last accessed time
     */
    public LocalDateTime getSessionLastAccessedAt(HttpSession session) {
        if (session != null) {
            long lastAccessedTime = session.getLastAccessedTime();
            return LocalDateTime.from(java.time.Instant.ofEpochMilli(lastAccessedTime)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        return null;
    }

    /**
     * Get session idle time in seconds
     */
    public long getSessionIdleSeconds(HttpSession session) {
        if (session != null) {
            long lastAccessedTime = session.getLastAccessedTime();
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastAccessedTime) / 1000;
        }
        return 0;
    }

    /**
     * Get session max inactive interval in seconds
     */
    public int getSessionMaxInactiveSeconds(HttpSession session) {
        return session != null ? session.getMaxInactiveInterval() : 0;
    }

    /**
     * Check if session is about to expire (within 5 minutes)
     */
    public boolean isSessionAboutToExpire(HttpSession session) {
        if (session != null) {
            long idleSeconds = getSessionIdleSeconds(session);
            int maxInactiveSeconds = getSessionMaxInactiveSeconds(session);
            // Warn if more than 25 minutes idle (timeout is 30 minutes)
            return idleSeconds > (maxInactiveSeconds - 300);
        }
        return false;
    }

    /**
     * Get session info as a friendly string
     */
    public String getSessionInfo(HttpSession session) {
        if (session == null) {
            return "No active session";
        }

        LocalDateTime createdAt = getSessionCreatedAt(session);
        LocalDateTime lastAccessedAt = getSessionLastAccessedAt(session);
        long idleSeconds = getSessionIdleSeconds(session);
        int maxInactiveSeconds = getSessionMaxInactiveSeconds(session);

        return String.format(
            "Session ID: %s | Created: %s | Last Accessed: %s | Idle: %d sec | Max Inactive: %d sec",
            session.getId(),
            createdAt,
            lastAccessedAt,
            idleSeconds,
            maxInactiveSeconds
        );
    }
}

