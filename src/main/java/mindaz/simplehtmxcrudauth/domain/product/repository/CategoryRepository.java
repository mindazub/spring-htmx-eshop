package mindaz.simplehtmxcrudauth.domain.product.repository;

import mindaz.simplehtmxcrudauth.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByDeletedFalse();
    Optional<Category> findByIdAndDeletedFalse(Long id);
    Optional<Category> findBySlugAndDeletedFalse(String slug);
}

