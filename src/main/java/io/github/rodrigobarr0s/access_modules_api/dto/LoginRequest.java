package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 4, max = 8, message = "Senha deve ter entre 4 e 8 caracteres")
        String password
) {
}
