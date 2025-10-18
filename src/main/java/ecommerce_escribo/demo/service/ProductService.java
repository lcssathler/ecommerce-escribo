package ecommerce_escribo.demo.service;

import ecommerce_escribo.demo.domain.Product;
import ecommerce_escribo.demo.dto.ProductDTO;
import ecommerce_escribo.demo.mapper.ProductMapper;
import ecommerce_escribo.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductDTO create(ProductDTO dto) {
        Product product = mapper.toEntity(dto);
        Product saved = repository.save(product);
        return mapper.toDTO(saved);
    }

    public List<ProductDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProductDTO findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return mapper.toDTO(product);
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());

        return mapper.toDTO(repository.save(product));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new EntityNotFoundException("Product not found: " + id);
        repository.deleteById(id);
    }
}
