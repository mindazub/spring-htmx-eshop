package mindaz.simplehtmxcrudauth.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import mindaz.simplehtmxcrudauth.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

