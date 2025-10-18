package ecommerce_escribo.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ClientDTO(
        Long id,

        @NotBlank(message = "client must have a name")
        @Size(min = 3, max = 100, message = "name must have between 3 and 100")
        String name,

        @Email(message = "invalid email")
        @NotBlank
        String email,

        LocalDateTime createdAt
) {

    public ClientDTO(String name, String email) {
        this(0L, name, email, LocalDateTime.now());
    }
}
