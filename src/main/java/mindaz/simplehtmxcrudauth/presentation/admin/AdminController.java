package mindaz.simplehtmxcrudauth.presentation.admin;

import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.domain.user.service.UserService;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("auditLogs", auditService.getLatestAuditLogs());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String email, @RequestParam String password,
                            @RequestParam String fullName, @RequestParam String role,
                            RedirectAttributes redirectAttributes) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            userService.createUser(email, password, fullName, userRole);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create user");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role/{role}")
    public String addRole(@PathVariable Long id, @PathVariable String role, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            userService.addRole(user, userRole);
            redirectAttributes.addFlashAttribute("success", "Role added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add role");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/audit")
    public String viewAuditLog(Model model) {
        model.addAttribute("auditLogs", auditService.getLatestAuditLogs());
        return "admin/audit";
    }
}

