package ecommerce_escribo.demo.mapper;

import ecommerce_escribo.demo.domain.OrderProduct;
import ecommerce_escribo.demo.dto.OrderProductDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {
    public OrderProductDTO toDTO(OrderProduct entity) {
        return new OrderProductDTO(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getProduct().getPrice(),
                entity.getSubtotal(),
                entity.getQuantity(),
                entity.getCreatedAt()
                );
    }
}
