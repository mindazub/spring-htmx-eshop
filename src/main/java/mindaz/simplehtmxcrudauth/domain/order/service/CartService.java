package mindaz.simplehtmxcrudauth.domain.order.service;

import mindaz.simplehtmxcrudauth.domain.order.entity.Cart;
import mindaz.simplehtmxcrudauth.domain.order.entity.CartItem;
import mindaz.simplehtmxcrudauth.domain.order.repository.CartRepository;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.service.ProductService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().user(user).build();
                    return cartRepository.save(cart);
                });
    }

    public Cart addToCart(User user, Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getOrCreateCart(user);
        Product product = productService.getProductById(productId);

        if (product.getQuantity() == null || product.getQuantity() < 1) {
            throw new IllegalArgumentException("Product is out of stock");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            int updatedQuantity = existingItem.get().getQuantity() + quantity;
            existingItem.get().setQuantity(Math.min(updatedQuantity, product.getQuantity()));
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(Math.min(quantity, product.getQuantity()))
                    .priceAtAddTime(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart removeFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(User user, Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getOrCreateCart(user);
        Product product = productService.getProductById(productId);
        int safeQuantity = Math.min(quantity, Math.max(product.getQuantity(), 1));
        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(safeQuantity));
        return cartRepository.save(cart);
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}

