package ecommerce_escribo.demo.service;

import ecommerce_escribo.demo.domain.Client;
import ecommerce_escribo.demo.dto.ClientDTO;
import ecommerce_escribo.demo.mapper.ClientMapper;
import ecommerce_escribo.demo.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;

    public ClientService(ClientRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ClientDTO create(ClientDTO dto) {
        Client client = mapper.toEntity(dto);
        Client saved = repository.save(client);
        return mapper.toDTO(saved);
    }

    public List<ClientDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ClientDTO findById(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + id));
        return mapper.toDTO(client);
    }

    public ClientDTO update(Long id, ClientDTO dto) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + id));

        client.setName(dto.name());
        client.setEmail(dto.email());

        return mapper.toDTO(repository.save(client));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new EntityNotFoundException("Client not found: " + id);
        repository.deleteById(id);
    }
}
