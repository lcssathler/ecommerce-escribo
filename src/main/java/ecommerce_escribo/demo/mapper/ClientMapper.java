package ecommerce_escribo.demo.mapper;

import ecommerce_escribo.demo.domain.Client;
import ecommerce_escribo.demo.dto.ClientDTO;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity(ClientDTO dto) {
        if (dto == null) return null;
        return new Client(dto.name(), dto.email());
    }

    public ClientDTO toDTO(Client entity) {
        if (entity == null) return null;
        return new ClientDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getCreatedAt());
    }
}
