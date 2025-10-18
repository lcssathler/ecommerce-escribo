package ecommerce_escribo.demo.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "product must have a name")
    @Size(min = 2, max = 100)
    private String name;

    @Column
    @NotNull(message = "product must have a price")
    private BigDecimal price;

    @Column
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
