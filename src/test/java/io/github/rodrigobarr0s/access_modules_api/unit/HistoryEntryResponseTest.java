package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.dto.HistoryEntryResponse;

class HistoryEntryResponseTest {

    @Test
    @DisplayName("Deve criar HistoryEntryResponse com valores corretos")
    void deveCriarHistoryEntryResponse() {
        LocalDateTime agora = LocalDateTime.now();

        HistoryEntryResponse response = new HistoryEntryResponse(
                "CANCELADO",
                "Solicitação não era mais necessária",
                agora
        );

        assertThat(response.action()).isEqualTo("CANCELADO");
        assertThat(response.reason()).isEqualTo("Solicitação não era mais necessária");
        assertThat(response.date()).isEqualTo(agora);
    }
}
