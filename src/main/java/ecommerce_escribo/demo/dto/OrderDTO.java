package ecommerce_escribo.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record OrderDTO(
        Long id,

        @NotNull(message = "oder must reference a client")
        Long clientId,

        @NotNull
        @NotEmpty(message = "list can't be empty")
        List<OrderProductDTO> listProductsToBuy,

        BigDecimal total,

        LocalDateTime createdAt,

        String status
) {
    public OrderDTO {
        if (listProductsToBuy == null) {
            listProductsToBuy = new ArrayList<>();
        } else {
            listProductsToBuy = List.copyOf(listProductsToBuy);
        }

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = "PENDING";
        }
    }

    public OrderDTO(Long clientId, List<OrderProductDTO> listProductsToBuy) {
        this(null, clientId, listProductsToBuy, null, null, null);
    }
}
