package io.github.rodrigobarr0s.access_modules_api.dto;

import java.time.LocalDateTime;

public record HistoryEntryResponse(
        String action, // Ex: CRIADO, CANCELADO, RENOVADO
        String reason, // Motivo se houver
        LocalDateTime date // Quando ocorreu
) {
}
