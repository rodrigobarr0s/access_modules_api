package io.github.rodrigobarr0s.access_modules_api.dto;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;

public record AccessSolicitationCreateResponse(
    String protocolo,
    String status,
    String message
) {
    public AccessSolicitationCreateResponse(AccessSolicitation s) {
        this(
            s.getProtocolo(),
            s.getStatus().name(),
            s.getStatus() == SolicitationStatus.ATIVO
                ? "Solicitação criada com sucesso! Protocolo: " + s.getProtocolo() +
                  ". Seus acessos já estão disponíveis!"
                : "Solicitação negada. Motivo: " + s.getNegationReason()
        );
    }
}
