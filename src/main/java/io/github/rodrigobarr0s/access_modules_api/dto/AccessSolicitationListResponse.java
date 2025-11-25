package io.github.rodrigobarr0s.access_modules_api.dto;

import java.time.LocalDateTime;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;

public record AccessSolicitationListResponse(
    String protocolo,
    String status,
    boolean urgente,
    Long moduleId,
    String moduleName,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {
    public AccessSolicitationListResponse(AccessSolicitation s) {
        this(
            s.getProtocolo(),
            s.getStatus().name(),
            s.isUrgente(),
            s.getModule().getId(),
            s.getModule().getName(),
            s.getCreatedAt(),
            s.getExpiresAt()
        );
    }
}
