package ecommerce_escribo.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
    @NotBlank
    @DecimalMin(value = "0.0",message = "price of product can't be negative")
    private BigDecimal price;

    @Column
    @Min(value = 0, message = "quantity of product can't be negative")
    private int quantity = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

