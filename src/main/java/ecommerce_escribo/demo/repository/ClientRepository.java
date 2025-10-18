package ecommerce_escribo.demo.repository;

import ecommerce_escribo.demo.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
