package mindaz.simplehtmxcrudauth.presentation.product;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mindaz.simplehtmxcrudauth.domain.product.service.FavoriteService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuditService auditService;

    @PostMapping("/toggle/{productId}")
    public String toggleFavorite(@PathVariable Long productId,
                                 Authentication authentication,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/login";
        }

        User user = (User) authentication.getPrincipal();
        FavoriteService.ToggleFavoriteResult result = favoriteService.toggle(user, productId);

        if (result == FavoriteService.ToggleFavoriteResult.ADDED) {
            redirectAttributes.addFlashAttribute("favoriteMessage", "Product added to favorites.");
        } else {
            redirectAttributes.addFlashAttribute("favoriteMessage", "Product removed from favorites.");
        }

        auditService.logEvent(AuditEvent.builder()
                .eventType(result == FavoriteService.ToggleFavoriteResult.ADDED ? "FAVORITE_ADDED" : "FAVORITE_REMOVED")
                .userId(user.getId())
                .userEmail(user.getEmail())
                .details("Product ID: " + productId)
                .timestamp(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build());

        String referer = request.getHeader("Referer");
        if (referer != null && (referer.contains("/products") || referer.contains("/account"))) {
            return "redirect:" + referer;
        }
        return "redirect:/products/" + productId;
    }
}

