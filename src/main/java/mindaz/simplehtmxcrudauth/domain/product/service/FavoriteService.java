package mindaz.simplehtmxcrudauth.domain.product.service;

import lombok.RequiredArgsConstructor;
import mindaz.simplehtmxcrudauth.domain.product.entity.FavoriteProduct;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.repository.FavoriteProductRepository;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteProductRepository favoriteProductRepository;
    private final ProductService productService;

    public ToggleFavoriteResult toggle(User user, Long productId) {
        return favoriteProductRepository.findByUserIdAndProductId(user.getId(), productId)
                .map(existing -> {
                    favoriteProductRepository.delete(existing);
                    return ToggleFavoriteResult.REMOVED;
                })
                .orElseGet(() -> {
                    Product product = productService.getProductById(productId);
                    FavoriteProduct favorite = FavoriteProduct.builder()
                            .user(user)
                            .product(product)
                            .build();
                    favoriteProductRepository.save(favorite);
                    return ToggleFavoriteResult.ADDED;
                });
    }

    public long countFavorites(Long userId) {
        return favoriteProductRepository.countByUserId(userId);
    }

    public Set<Long> getFavoriteProductIds(Long userId) {
        return favoriteProductRepository.findByUserId(userId).stream()
                .map(favorite -> favorite.getProduct().getId())
                .collect(Collectors.toSet());
    }

    public enum ToggleFavoriteResult {
        ADDED,
        REMOVED
    }
}

