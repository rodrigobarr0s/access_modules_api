package io.github.rodrigobarr0s.access_modules_api.dto;

import java.time.LocalDateTime;

public record AccessSolicitationResponse(
        String protocolo,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        String username,
        Long moduleId,
        String moduleName) {
}
