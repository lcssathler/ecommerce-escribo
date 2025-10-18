package ecommerce_escribo.demo.repository;

import ecommerce_escribo.demo.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
