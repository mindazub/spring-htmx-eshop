package mindaz.simplehtmxcrudauth.domain.product.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String slug;
    private String icon;

    @Builder.Default
    private Boolean deleted = false;
}

