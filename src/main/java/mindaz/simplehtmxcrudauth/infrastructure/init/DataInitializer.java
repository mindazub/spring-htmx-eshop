package mindaz.simplehtmxcrudauth.infrastructure.init;

import mindaz.simplehtmxcrudauth.domain.product.entity.Category;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import mindaz.simplehtmxcrudauth.domain.product.entity.ProductImage;
import mindaz.simplehtmxcrudauth.domain.product.repository.CategoryRepository;
import mindaz.simplehtmxcrudauth.domain.product.repository.ProductRepository;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;
import mindaz.simplehtmxcrudauth.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    private final String[] PEXELS_IMAGE_URLS = {
        "https://images.pexels.com/photos/3407857/pexels-photo-3407857.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/17809178/pexels-photo-17809178/free-photo-of-cpu.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/2625325/pexels-photo-2625325.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/159454/gamer-gaming-setup-online-multiplayer-159454.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/3808517/pexels-photo-3808517.jpeg?auto=compress&cs=tinysrgb&w=400",
        "https://images.pexels.com/photos/3587620/pexels-photo-3587620.jpeg?auto=compress&cs=tinysrgb&w=400"
    };

    @Override
    public void run(String... args) throws Exception {
        ensureAccountsActive();

        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // Create Users
        createUsers();

        // Create Categories
        List<Category> categories = createCategories();

        // Create 200 Products
        createProducts(categories);
    }

    private void ensureAccountsActive() {
        List<User> users = userRepository.findAllByDeletedFalse();
        boolean changed = false;

        for (User user : users) {
            if (Boolean.FALSE.equals(user.getEnabled())) {
                user.setEnabled(true);
                changed = true;
            }
            if (Boolean.FALSE.equals(user.getAccountNonLocked())) {
                user.setAccountNonLocked(true);
                changed = true;
            }
            if (Boolean.FALSE.equals(user.getAccountNonExpired())) {
                user.setAccountNonExpired(true);
                changed = true;
            }
            if (Boolean.FALSE.equals(user.getCredentialsNonExpired())) {
                user.setCredentialsNonExpired(true);
                changed = true;
            }
        }

        if (changed) {
            userRepository.saveAll(users);
        }
    }

    private void createUsers() {
        // Admin user
        User admin = User.builder()
                .email("admin@admin.com")
                .password(passwordEncoder.encode("admin000"))
                .fullName("Admin User")
                .roles(Set.of(User.UserRole.ADMIN))
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(admin);

        // Manager user
        User manager = User.builder()
                .email("manager@example.com")
                .password(passwordEncoder.encode("manager000"))
                .fullName("Manager User")
                .roles(Set.of(User.UserRole.MANAGER))
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(manager);

        // 5 Regular users
        for (int i = 1; i <= 5; i++) {
            User user = User.builder()
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("user" + i + "000"))
                    .fullName("User " + i)
                    .roles(Set.of(User.UserRole.USER))
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .build();
            userRepository.save(user);
        }
    }

    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();

        Category[] categoryData = {
            Category.builder().name("CPUs").slug("cpus").description("Processors and CPUs").icon("🔧").build(),
            Category.builder().name("GPUs").slug("gpus").description("Graphics Cards").icon("🎮").build(),
            Category.builder().name("Motherboards").slug("motherboards").description("Motherboards").icon("⚙️").build(),
            Category.builder().name("RAM").slug("ram").description("Memory Modules").icon("💾").build(),
            Category.builder().name("Storage").slug("storage").description("Storage Drives").icon("💿").build()
        };

        for (Category cat : categoryData) {
            categories.add(categoryRepository.save(cat));
        }

        return categories;
    }

    private void createProducts(List<Category> categories) {
        String[] cpuNames = {
            "Intel Core i9-13900K", "AMD Ryzen 9 7950X", "Intel Core i7-13700K", "AMD Ryzen 7 7700X",
            "Intel Core i5-13600K", "AMD Ryzen 5 7600X", "Intel Core i3-13100", "AMD Ryzen 3 7100",
            "Intel Xeon W9-3595X", "AMD Ryzen 9 7900X"
        };

        String[] gpuNames = {
            "RTX 4090", "RTX 4080", "RTX 4070 Ti", "RX 7900 XTX", "RX 7900 XT",
            "RTX 4070", "RTX 4060 Ti", "RX 7800 XT", "RX 7700 XT", "RTX 3060 Ti"
        };

        String[] motherboardNames = {
            "ASUS ROG Maximus Z790", "MSI MEG Z790 ACE", "Gigabyte Z790 Master", "ASRock Z790 Phantom Gaming",
            "ASUS ROG Strix B850-E", "MSI MPG B850 EDGE", "Gigabyte B850 Master", "ASRock B850M Phantom Gaming"
        };

        String[] ramNames = {
            "Corsair Dominator Platinum 32GB", "G.Skill Trident Z5 32GB", "Kingston Fury Beast 32GB",
            "Crucial Ballistix 32GB", "ADATA XPG Spectrix D80 32GB", "Patriot Viper Steel 32GB"
        };

        String[] storageNames = {
            "Samsung 990 Pro 4TB", "WD Black SN850X 4TB", "Sabrent Rocket 4 Plus 4TB",
            "Kingston A3000 2TB", "Crucial P5 Plus 2TB", "Seagate FireCuda 530 2TB"
        };

        int productCount = 0;
        int[] productsPerCategory = {40, 40, 40, 40, 40};
        String[][] nameArrays = {cpuNames, gpuNames, motherboardNames, ramNames, storageNames};

        for (int catIdx = 0; catIdx < categories.size(); catIdx++) {
            Category category = categories.get(catIdx);
            String[] names = nameArrays[catIdx];

            for (int i = 0; i < productsPerCategory[catIdx]; i++) {
                String baseName = names[i % names.length];
                String productName = baseName + " - Variant " + (i / names.length + 1);

                Product product = Product.builder()
                        .name(productName)
                        .description("High-quality " + category.getName().toLowerCase() + " for gaming and professional use. " +
                                   "Exceptional performance, reliability, and value for money.")
                        .price(BigDecimal.valueOf(Math.random() * 3000 + 100).setScale(2, java.math.RoundingMode.HALF_UP))
                        .category(category)
                        .sku("SKU-" + System.nanoTime() % 100000)
                        .quantity((int)(Math.random() * 100) + 1)
                        .rating(Math.random() * 2 + 3.5)
                        .reviewCount((int)(Math.random() * 500))
                        .build();

                // Add random images
                for (int imgIdx = 0; imgIdx < 3; imgIdx++) {
                    ProductImage image = ProductImage.builder()
                            .product(product)
                            .imageUrl(PEXELS_IMAGE_URLS[productCount % PEXELS_IMAGE_URLS.length])
                            .displayOrder(imgIdx)
                            .source("PEXELS")
                            .build();
                    product.getImages().add(image);
                }

                productRepository.save(product);
                productCount++;

                if (productCount >= 200) {
                    return;
                }
            }
        }
    }
}

