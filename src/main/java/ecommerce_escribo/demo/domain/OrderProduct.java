package ecommerce_escribo.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_product")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "mandatory order")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Order order;

    @NotNull(message = "mandatory product")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "order must have a defined quantity")
    @Min(value = 1, message = "minimum quantity of order is 1")
    private int quantity = 1;

    @NotNull
    @DecimalMin(value = "0.01", message = "unity price must be over than 0")
    @Column(name = "unity_price")
    private BigDecimal unityPrice;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
