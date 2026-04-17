package mindaz.simplehtmxcrudauth.presentation.auth;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.domain.user.service.UserService;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuditService auditService;

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String expired,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        if (expired != null) {
            model.addAttribute("expired", "Your session has expired. Please login again.");
        }
        return "auth/login";
    }

    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    /**
     * Handle registration form submission
     */
    @PostMapping("/register")
    public String register(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String passwordConfirm,
                          @RequestParam String fullName,
                          RedirectAttributes redirectAttributes) {
        try {
            // Validate form input
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
                return "redirect:/register";
            }

            if (fullName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Full name is required");
                return "redirect:/register";
            }

            // Check if user already exists
            if (userService.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already exists. Please use a different email or login.");
                return "redirect:/register";
            }

            // Create new user
            userService.createUser(email, password, fullName, User.UserRole.USER);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login with your new account.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Track successful login (called after Spring Security authenticates)
     * This is intercepted by Spring Security's form login success
     */
    @GetMapping("/login-success")
    public String loginSuccess(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            // Update last login timestamp
            userService.updateLastLogin(user);

            // Log the event
            auditService.logEvent(AuditEvent.builder()
                    .eventType("LOGIN")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build());
        }
        return "redirect:/products";
    }

    /**
     * Get current user info (for AJAX/API calls)
     */
    @GetMapping("/api/user-info")
    @ResponseBody
    public UserInfoDTO getCurrentUserInfo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return new UserInfoDTO(user.getId(), user.getEmail(), user.getFullName(), user.getRoles());
        }
        return null;
    }

    /**
     * DTO for user information
     */
    public static class UserInfoDTO {
        public Long id;
        public String email;
        public String fullName;
        public Object roles;

        public UserInfoDTO(Long id, String email, String fullName, Object roles) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.roles = roles;
        }
    }
}

