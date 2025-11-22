package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelSolicitationRequest(
        @NotNull @Size(min = 10, max = 200) String motivo
) {}
