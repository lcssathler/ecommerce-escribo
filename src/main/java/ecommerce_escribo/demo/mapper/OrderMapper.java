package ecommerce_escribo.demo.mapper;

import ecommerce_escribo.demo.domain.Order;
import ecommerce_escribo.demo.domain.OrderProduct;
import ecommerce_escribo.demo.dto.OrderDTO;
import ecommerce_escribo.demo.dto.OrderProductDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final OrderProductMapper orderProductMapper;

    public OrderMapper(OrderProductMapper itemMapper) {
        this.orderProductMapper = itemMapper;
    }

    public OrderDTO toDTO(Order entity) {
        List<OrderProduct> orderItem = entity.getListProductsToBuy();
        List<OrderProductDTO> orderItemsDTO = orderItem.stream().map(orderProductMapper::toDTO).toList();

        return new OrderDTO(
                entity.getId(),
                entity.getClient().getId(),
                orderItemsDTO,
                entity.getTotal(),
                entity.getCreatedAt(),
                entity.getStatus().toString()
                );
    }
}
