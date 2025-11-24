package io.github.rodrigobarr0s.access_modules_api.dto;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;

public record AccessSolicitationResponse(
        String protocolo,
        String status,
        String justificativa,
        boolean urgente,
        Long userId,
        String userEmail,
        Long moduleId,
        String moduleName,
        String cancelReason,
        String negationReason
) {
    public AccessSolicitationResponse(AccessSolicitation solicitation) {
        this(
            solicitation.getProtocolo(),
            solicitation.getStatus().name(),
            solicitation.getJustificativa(),
            solicitation.isUrgente(),
            solicitation.getUser() != null ? solicitation.getUser().getId() : null,
            solicitation.getUser() != null ? solicitation.getUser().getEmail() : null,
            solicitation.getModule() != null ? solicitation.getModule().getId() : null,
            solicitation.getModule() != null ? solicitation.getModule().getName() : null,
            solicitation.getCancelReason(),
            solicitation.getNegationReason()
        );
    }
}
