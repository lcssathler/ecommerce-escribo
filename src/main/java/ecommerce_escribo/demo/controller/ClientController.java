package ecommerce_escribo.demo.controller;

import ecommerce_escribo.demo.dto.ClientDTO;
import ecommerce_escribo.demo.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClientDTO> create(
            @Valid @RequestBody ClientDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        ClientDTO created = service.create(dto, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> findAll(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        return ResponseEntity.ok(service.findAll(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        return ResponseEntity.ok(service.findById(id, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> update(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @RequestBody @Valid ClientDTO dto
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        return ResponseEntity.ok(service.update(id, dto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        service.delete(id, token);
        return ResponseEntity.noContent().build();
    }
}