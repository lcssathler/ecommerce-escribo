package ecommerce_escribo.demo.repository;

import ecommerce_escribo.demo.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
