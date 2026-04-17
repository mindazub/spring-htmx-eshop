package mindaz.simplehtmxcrudauth.presentation.auth;

import lombok.RequiredArgsConstructor;
import mindaz.simplehtmxcrudauth.domain.order.service.CartService;
import mindaz.simplehtmxcrudauth.domain.product.service.FavoriteService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final FavoriteService favoriteService;
    private final AuditService auditService;
    private final CartService cartService;

    @GetMapping
    public String accountOverview(Authentication authentication, Model model) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/login";
        }

        User user = (User) authentication.getPrincipal();

        model.addAttribute("authenticated", true);
        model.addAttribute("currentUserEmail", user.getEmail());
        model.addAttribute("admin", user.getRoles().contains(User.UserRole.ADMIN));
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("accountActive", user.isEnabled());

        long completedOrdersCount = auditService.countUserEvents(user.getId(), "CHECKOUT_SUCCESS");
        long favoriteProductsCount = favoriteService.countFavorites(user.getId());
        int cartItemsCount = cartService.getOrCreateCart(user).getItems().size();

        model.addAttribute("completedOrdersCount", completedOrdersCount);
        model.addAttribute("favoriteProductsCount", favoriteProductsCount);
        model.addAttribute("cartItemsCount", cartItemsCount);
        model.addAttribute("recentCheckoutEvents", auditService.getLatestUserEvents(user.getId(), "CHECKOUT_SUCCESS"));

        return "auth/account";
    }
}

