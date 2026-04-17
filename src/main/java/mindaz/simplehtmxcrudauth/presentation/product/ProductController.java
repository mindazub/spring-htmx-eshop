package mindaz.simplehtmxcrudauth.presentation.product;

import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.service.FavoriteService;
import mindaz.simplehtmxcrudauth.domain.product.service.ProductService;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.shared.audit.service.AuditService;
import mindaz.simplehtmxcrudauth.shared.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FavoriteService favoriteService;
    private final AuditService auditService;

    private static final int DEFAULT_PAGE_SIZE = 9;

    @GetMapping
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "9") int size,
                               @RequestParam(required = false) String login,
                               Model model,
                               Authentication authentication,
                               HttpServletRequest request) {
        logPageView(authentication, "/products", request);
        addCommonModelAttributes(model, authentication);
        addFavoriteModelAttributes(model, authentication);

        if ("success".equalsIgnoreCase(login)) {
            model.addAttribute("notice", "Login successful. Welcome back!");
        }

        Page<Product> productPage = productService.getProductsPage(page, size);
        addPaginationAttributes(model, productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("listType", "all");
        model.addAttribute("categories", productService.getAllCategories());
        return "products/list";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request) {
        logPageView(authentication, "/products/" + id, request);
        addCommonModelAttributes(model, authentication);
        addFavoriteModelAttributes(model, authentication);

        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            model.addAttribute("isFavorite", favoriteService.getFavoriteProductIds(user.getId()).contains(id));
        } else {
            model.addAttribute("isFavorite", false);
        }
        return "products/detail";
    }

    @GetMapping("/category/{categoryId}")
    public String viewByCategory(@PathVariable Long categoryId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "9") int size,
                                 Model model,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        logPageView(authentication, "/products/category/" + categoryId, request);
        addCommonModelAttributes(model, authentication);
        addFavoriteModelAttributes(model, authentication);

        Page<Product> productPage = productService.getProductsByCategoryPage(categoryId, page, size);
        addPaginationAttributes(model, productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("listType", "category");
        model.addAttribute("currentCategoryId", categoryId);
        model.addAttribute("categories", productService.getAllCategories());
        return "products/list";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String query,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "9") int size,
                                 Model model,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        logPageView(authentication, "/products/search?q=" + query, request);
        addCommonModelAttributes(model, authentication);
        addFavoriteModelAttributes(model, authentication);

        Page<Product> productPage = productService.searchProductsPage(query, page, size);
        addPaginationAttributes(model, productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("searchQuery", query);
        model.addAttribute("listType", "search");
        model.addAttribute("categories", productService.getAllCategories());
        return "products/list";
    }

    private void addPaginationAttributes(Model model, Page<Product> productPage) {
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", productPage.getSize() > 0 ? productPage.getSize() : DEFAULT_PAGE_SIZE);
    }

    private void logPageView(Authentication authentication, String page, HttpServletRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            auditService.logEvent(AuditEvent.builder()
                    .eventType("PAGE_VIEW")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .details("Page: " + page)
                    .timestamp(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build());
        }
    }

    private void addCommonModelAttributes(Model model, Authentication authentication) {
        boolean authenticated = authentication != null && authentication.getPrincipal() instanceof User;
        model.addAttribute("authenticated", authenticated);

        if (authenticated) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("currentUserEmail", user.getEmail());
            model.addAttribute("admin", user.getRoles().contains(User.UserRole.ADMIN));
        } else {
            model.addAttribute("currentUserEmail", null);
            model.addAttribute("admin", false);
        }
    }

    private void addFavoriteModelAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            model.addAttribute("favoriteProductIds", favoriteService.getFavoriteProductIds(user.getId()));
        } else {
            model.addAttribute("favoriteProductIds", java.util.Collections.emptySet());
        }
    }
}

