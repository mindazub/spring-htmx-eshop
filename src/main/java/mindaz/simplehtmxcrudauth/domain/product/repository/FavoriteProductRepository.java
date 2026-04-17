package mindaz.simplehtmxcrudauth.domain.product.repository;

import mindaz.simplehtmxcrudauth.domain.product.entity.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    Optional<FavoriteProduct> findByUserIdAndProductId(Long userId, Long productId);
    long countByUserId(Long userId);
    List<FavoriteProduct> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    List<FavoriteProduct> findByUserId(Long userId);
}

