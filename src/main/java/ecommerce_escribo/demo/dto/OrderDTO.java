package ecommerce_escribo.demo.dto;

import ecommerce_escribo.demo.domain.Client;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        Long id,

        @NotNull(message = "oder must reference a client")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "client_id")
        Long clientId,

        List<OrderProductDTO> listProductsToBuy
) {
    public OrderDTO(List<OrderProductDTO> listProductsToBuy, Long clientId) {
        this(0L, clientId, listProductsToBuy);
    }
}
