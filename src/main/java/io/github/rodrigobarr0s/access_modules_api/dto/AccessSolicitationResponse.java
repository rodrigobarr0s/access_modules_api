package io.github.rodrigobarr0s.access_modules_api.dto;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;

public record AccessSolicitationResponse(String protocolo, String status, String justificativa, boolean urgente) {
    public AccessSolicitationResponse(AccessSolicitation solicitation) {
        this(solicitation.getProtocolo(), solicitation.getStatus().name(), solicitation.getJustificativa(), solicitation.isUrgente());
    }
}

