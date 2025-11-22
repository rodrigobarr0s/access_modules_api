package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccessSolicitationRequest(
        @NotNull Long userId,
        @NotNull Long moduleId,
        @NotNull @Size(min = 5, max = 255) String justificativa) {
}
