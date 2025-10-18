package ecommerce_escribo.demo.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDTO(
        Long id,

        @NotNull
        @NotBlank(message = "product must have a name")
        @Size(min = 2, max = 100)
        String name,

        @NotNull
        @NotBlank
        @DecimalMin(value = "0.0",message = "price of product can't be negative")
        BigDecimal price,

        @Min(value = 0, message = "quantity of product can't be negative")
        int quantity,

        LocalDateTime createdAt
        ) {

    public ProductDTO(String name, BigDecimal price, int quantity) {
        this(0L, name, price, quantity, LocalDateTime.now());
    }
}
