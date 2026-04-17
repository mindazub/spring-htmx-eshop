package mindaz.simplehtmxcrudauth.presentation.cart;

import mindaz.simplehtmxcrudauth.domain.order.service.CartService;
import mindaz.simplehtmxcrudauth.domain.order.service.FakeCheckoutService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final FakeCheckoutService fakeCheckoutService;
    private final AuditService auditService;

    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(9.99);
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.10);

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = extractUser(authentication);
        if (user != null) {
            addCommonModelAttributes(model, user);
            var cart = cartService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
            addCartTotals(model, cart.getTotalPrice());
            return "cart/view";
        }
        return "redirect:/login";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            Authentication authentication,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        User user = extractUser(authentication);
        if (user != null) {
            try {
                cartService.addToCart(user, productId, quantity);
                redirectAttributes.addFlashAttribute("cartMessage", "Product added to cart.");
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
                return "redirect:/products/" + productId;
            }

            auditService.logEvent(AuditEvent.builder()
                    .eventType("CART_ADDED")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .details("Product ID: " + productId + ", Quantity: " + quantity)
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build());

            return "redirect:/cart";
        }
        return "redirect:/login";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = extractUser(authentication);
        if (user != null) {
            cartService.removeFromCart(user, productId);
            redirectAttributes.addFlashAttribute("cartMessage", "Product removed from cart.");
            return "redirect:/cart";
        }
        return "redirect:/login";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam Integer quantity,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = extractUser(authentication);
        if (user != null) {
            if (quantity > 0) {
                try {
                    cartService.updateCartItemQuantity(user, productId, quantity);
                    redirectAttributes.addFlashAttribute("cartMessage", "Cart updated.");
                } catch (IllegalArgumentException ex) {
                    redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
                }
            }
            return "redirect:/cart";
        }
        return "redirect:/login";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        User user = extractUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }

        var cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("cartError", "Your cart is empty. Add at least one product before checkout.");
            return "redirect:/cart";
        }

        addCommonModelAttributes(model, user);
        model.addAttribute("cart", cart);
        model.addAttribute("checkoutTotal", cart.getTotalPrice());
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String completeCheckout(@RequestParam String cardHolder,
                                   @RequestParam String cardNumber,
                                   @RequestParam String expiryMonth,
                                   @RequestParam String expiryYear,
                                   @RequestParam String cvc,
                                   Authentication authentication,
                                   HttpServletRequest request,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User user = extractUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            FakeCheckoutService.CheckoutResult result = fakeCheckoutService.process(user, cardHolder, cardNumber, expiryMonth, expiryYear, cvc);

            auditService.logEvent(AuditEvent.builder()
                    .eventType("CHECKOUT_SUCCESS")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .details("PaymentId: " + result.paymentId() + ", Amount: " + result.totalAmount())
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build());

            addCommonModelAttributes(model, user);
            model.addAttribute("checkoutResult", result);
            return "cart/success";
        } catch (IllegalArgumentException ex) {
            auditService.logEvent(AuditEvent.builder()
                    .eventType("CHECKOUT_FAILED")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .details(ex.getMessage())
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build());

            redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    private User extractUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    private void addCartTotals(Model model, BigDecimal subtotal) {
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE);
        BigDecimal grandTotal = subtotal.add(SHIPPING_FEE).add(taxAmount);
        model.addAttribute("shippingFee", SHIPPING_FEE);
        model.addAttribute("taxAmount", taxAmount);
        model.addAttribute("grandTotal", grandTotal);
    }

    private void addCommonModelAttributes(Model model, User user) {
        model.addAttribute("authenticated", true);
        model.addAttribute("currentUserEmail", user.getEmail());
        model.addAttribute("admin", user.getRoles().contains(User.UserRole.ADMIN));
    }
}

