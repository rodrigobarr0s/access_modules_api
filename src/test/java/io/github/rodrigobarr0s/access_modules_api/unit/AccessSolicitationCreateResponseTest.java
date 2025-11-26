package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationCreateResponse;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;

class AccessSolicitationCreateResponseTest {

    @Test
    @DisplayName("Deve criar resposta com mensagem de sucesso quando status é ATIVO")
    void deveCriarRespostaComSucesso() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");
        solicitation.setStatus(SolicitationStatus.ATIVO);

        AccessSolicitationCreateResponse response = new AccessSolicitationCreateResponse(solicitation);

        assertThat(response.protocolo()).isEqualTo("PROTO123");
        assertThat(response.status()).isEqualTo("ATIVO");
        assertThat(response.message())
                .contains("Solicitação criada com sucesso! Protocolo: PROTO123");
    }

    @Test
    @DisplayName("Deve criar resposta com mensagem de negação quando status é NEGADO")
    void deveCriarRespostaComNegacao() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO456");
        solicitation.setStatus(SolicitationStatus.NEGADO);
        solicitation.setNegationReason("Justificativa insuficiente");

        AccessSolicitationCreateResponse response = new AccessSolicitationCreateResponse(solicitation);

        assertThat(response.protocolo()).isEqualTo("PROTO456");
        assertThat(response.status()).isEqualTo("NEGADO");
        assertThat(response.message())
                .contains("Solicitação negada. Motivo: Justificativa insuficiente");
    }
}
