package ecommerce_escribo.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderProductDTO(
        Long id,

        @NotNull(message = "mandatory product")
        Long productId,

        BigDecimal unitPrice,

        BigDecimal subtotal,

        @NotNull(message = "order must have a defined quantity")
        @Min(value = 1, message = "minimum quantity of order is 1")
        int quantity,

        LocalDateTime createdAt
) {
    public OrderProductDTO(Long orderId, Long productId, int quantity) {
        this(0L, productId, BigDecimal.ZERO, BigDecimal.ZERO, quantity, LocalDateTime.now());
    }
}
