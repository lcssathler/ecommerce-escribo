package ecommerce_escribo.demo.service;

import ecommerce_escribo.demo.domain.Client;
import ecommerce_escribo.demo.dto.ClientDTO;
import ecommerce_escribo.demo.mapper.ClientMapper;
import ecommerce_escribo.demo.repository.ClientRepository;
import ecommerce_escribo.demo.util.SupabaseAuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ClientService {

    private final WebClient webClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    public ClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ClientDTO create(ClientDTO dto, String authorizationHeader) {
        String userId = SupabaseAuthUtil.extractUserId(authorizationHeader);

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", dto.name());
        payload.put("email", dto.email());
        payload.put("user_id", userId);

        System.out.println("Payload enviado ao Supabase: " + payload);

        return webClient.post()
                .uri(supabaseUrl + "/rest/v1/client")
                .header("apikey", supabaseAnonKey)
                .header("Authorization", authorizationHeader)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .header("Accept-Profile", "public")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ClientDTO>>() {})
                .map(list -> list.isEmpty() ? null : list.get(0))
                .block();
    }

    public List<ClientDTO> findAll(String authorizationHeader) {
        try {
            return webClient.get()
                    .uri(supabaseUrl + "/rest/v1/client?select=*")
                    .header("apikey", supabaseAnonKey)
                    .header("Authorization", authorizationHeader.startsWith("Bearer") ? authorizationHeader : "Bearer " + authorizationHeader)
                    .header("Accept-Profile", "public")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ClientDTO>>() {})
                    .blockOptional()
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Erro ao buscar clientes: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public ClientDTO findById(Long id, String token) {
        ClientDTO[] result = webClient.get()
                .uri(supabaseUrl + "/rest/v1/client?id=eq." + id + "&select=*")
                .header("apikey", supabaseAnonKey)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(ClientDTO[].class)
                .block();

        if (result == null || result.length == 0)
            throw new RuntimeException("Client not found: " + id);

        return result[0];
    }


    public ClientDTO update(Long id, ClientDTO dto, String token) {
        ClientDTO[] result = webClient.patch()
                .uri(supabaseUrl + "/rest/v1/client?id=eq." + id)
                .header("apikey", supabaseAnonKey)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ClientDTO[].class)
                .block();

        if (result == null || result.length == 0)
            throw new RuntimeException("Client not found: " + id);

        return result[0];
    }

    public void delete(Long id, String token) {
        webClient.delete()
                .uri(supabaseUrl + "/rest/v1/client?id=eq." + id)
                .header("apikey", supabaseAnonKey)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
