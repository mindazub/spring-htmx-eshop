package mindaz.simplehtmxcrudauth.domain.product.repository;

import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalse();
    Page<Product> findAllByDeletedFalse(Pageable pageable);
    Optional<Product> findByIdAndDeletedFalse(Long id);
    List<Product> findByCategoryAndDeletedFalse(Category category);
    Page<Product> findByCategoryAndDeletedFalse(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchByNameOrDescription(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchByNameOrDescription(@Param("query") String query, Pageable pageable);
}

