package mindaz.simplehtmxcrudauth.domain.order.service;

import mindaz.simplehtmxcrudauth.domain.order.entity.Cart;
import mindaz.simplehtmxcrudauth.domain.order.entity.CartItem;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.service.ProductService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FakeCheckoutService {

    private static final Set<String> ACCEPTED_TEST_CARDS = Set.of("42424242", "4242424242424242");

    private final CartService cartService;
    private final ProductService productService;

    public CheckoutResult process(User user,
                                  String cardHolder,
                                  String cardNumber,
                                  String expiryMonth,
                                  String expiryYear,
                                  String cvc) {
        validatePaymentInput(cardHolder, cardNumber, expiryMonth, expiryYear, cvc);

        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        for (CartItem item : cart.getItems()) {
            Product latestProduct = productService.getProductById(item.getProduct().getId());
            if (latestProduct.getQuantity() == null || latestProduct.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + latestProduct.getName());
            }
        }

        for (CartItem item : cart.getItems()) {
            Product latestProduct = productService.getProductById(item.getProduct().getId());
            latestProduct.setQuantity(latestProduct.getQuantity() - item.getQuantity());
            productService.updateProduct(latestProduct);
        }

        BigDecimal totalAmount = cart.getTotalPrice();
        String paymentId = "pay_test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        cartService.clearCart(user);

        return new CheckoutResult(paymentId, totalAmount, LocalDateTime.now());
    }

    private void validatePaymentInput(String cardHolder,
                                      String cardNumber,
                                      String expiryMonth,
                                      String expiryYear,
                                      String cvc) {
        if (cardHolder == null || cardHolder.isBlank()) {
            throw new IllegalArgumentException("Card holder name is required.");
        }
        if (expiryMonth == null || expiryMonth.isBlank() || expiryYear == null || expiryYear.isBlank()) {
            throw new IllegalArgumentException("Card expiry is required.");
        }
        if (cvc == null || cvc.isBlank()) {
            throw new IllegalArgumentException("CVC is required.");
        }

        String normalizedCard = normalizeCard(cardNumber);
        if (!ACCEPTED_TEST_CARDS.contains(normalizedCard)) {
            throw new IllegalArgumentException("Use test card 42424242 to simulate a successful payment.");
        }
    }

    private String normalizeCard(String cardNumber) {
        if (cardNumber == null) {
            return "";
        }
        return cardNumber.replaceAll("\\D", "");
    }

    public record CheckoutResult(String paymentId, BigDecimal totalAmount, LocalDateTime paidAt) {}
}

