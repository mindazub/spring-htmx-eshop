package mindaz.simplehtmxcrudauth.domain.product.service;

import mindaz.simplehtmxcrudauth.domain.product.entity.Category;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.repository.ProductRepository;
import mindaz.simplehtmxcrudauth.domain.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllByDeletedFalse();
    }

    public Page<Product> getProductsPage(int page, int size) {
        return productRepository.findAllByDeletedFalse(buildPageable(page, size));
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
        return productRepository.findByCategoryAndDeletedFalse(category);
    }

    public Page<Product> getProductsByCategoryPage(Long categoryId, int page, int size) {
        Category category = categoryRepository.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
        return productRepository.findByCategoryAndDeletedFalse(category, buildPageable(page, size));
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchByNameOrDescription(query);
    }

    public Page<Product> searchProductsPage(String query, int page, int size) {
        return productRepository.searchByNameOrDescription(query, buildPageable(page, size));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByDeletedFalse();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 24);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "name"));
    }
}

