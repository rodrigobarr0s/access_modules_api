package io.github.rodrigobarr0s.access_modules_api.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;

public record AccessSolicitationDetailResponse(
    String protocolo,
    String status,
    String justificativa,
    boolean urgente,
    Long userId,
    String userEmail,
    Long moduleId,
    String moduleName,
    String cancelReason,
    String negationReason,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    List<HistoryEntryResponse> history
) {
    public AccessSolicitationDetailResponse(AccessSolicitation s) {
        this(
            s.getProtocolo(),
            s.getStatus().name(),
            s.getJustificativa(),
            s.isUrgente(),
            s.getUser().getId(),
            s.getUser().getEmail(),
            s.getModule().getId(),
            s.getModule().getName(),
            s.getCancelReason(),
            s.getNegationReason(),
            s.getCreatedAt(),
            s.getExpiresAt(),
            s.getHistory().stream()
                .map(h -> new HistoryEntryResponse(h.getAction().name(), h.getReason(), h.getDate()))
                .toList()
        );
    }
}
