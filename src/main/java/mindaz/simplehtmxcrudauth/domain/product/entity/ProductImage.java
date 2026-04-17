package mindaz.simplehtmxcrudauth.domain.product.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Builder.Default
    private Integer displayOrder = 0;
    private String source; // e.g., "PEXELS", "UNSPLASH", "CUSTOM"
}

