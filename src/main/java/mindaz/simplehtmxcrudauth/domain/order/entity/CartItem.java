package mindaz.simplehtmxcrudauth.domain.order.entity;

import lombok.*;
import mindaz.simplehtmxcrudauth.domain.product.entity.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    private BigDecimal priceAtAddTime;

    public BigDecimal getTotalPrice() {
        return priceAtAddTime != null ? priceAtAddTime.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }
}

