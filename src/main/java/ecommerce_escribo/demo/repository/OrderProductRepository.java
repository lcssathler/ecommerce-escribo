package ecommerce_escribo.demo.repository;

import ecommerce_escribo.demo.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
