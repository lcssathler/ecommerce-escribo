package ecommerce_escribo.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductDTO(
        Long id,

        @NotNull(message = "mandatory order")
        Long orderId,

        @NotNull(message = "mandatory product")
        Long productId,

        @NotNull(message = "order must have a defined quantity")
        @Min(value = 1, message = "minimum quantity of order is 1")
        int quantity
) {
    public OrderProductDTO(Long orderId, Long productId, int quantity) {
        this(0L, orderId, productId, quantity);
    }
}
