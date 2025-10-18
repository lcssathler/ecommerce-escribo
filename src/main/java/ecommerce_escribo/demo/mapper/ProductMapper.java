package ecommerce_escribo.demo.mapper;

import ecommerce_escribo.demo.domain.Product;
import ecommerce_escribo.demo.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        return new Product(dto.name(), dto.price(), dto.quantity());
    }

    public ProductDTO toDTO(Product entity) {
        if (entity == null) return null;
        return new ProductDTO(entity.getId(), entity.getName(), entity.getPrice(), entity.getQuantity(), entity.getCreatedAt());
    }
}
