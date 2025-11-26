package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.SolicitationHistory;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.HistoryAction;

class SolicitationHistoryTest {

    @Test
    @DisplayName("Deve criar SolicitationHistory com construtor completo")
    void deveCriarComConstrutorCompleto() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");

        LocalDateTime agora = LocalDateTime.now();

        SolicitationHistory history = new SolicitationHistory(
                1L,
                solicitation,
                HistoryAction.CRIADO,
                "Motivo inicial",
                agora);

        assertThat(history.getId()).isEqualTo(1L);
        assertThat(history.getSolicitation()).isEqualTo(solicitation);
        assertThat(history.getAction()).isEqualTo(HistoryAction.CRIADO);
        assertThat(history.getReason()).isEqualTo("Motivo inicial");
        assertThat(history.getDate()).isEqualTo(agora);
    }

    @Test
    @DisplayName("Deve criar SolicitationHistory com construtor parcial e data atual")
    void deveCriarComConstrutorParcial() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO456");

        SolicitationHistory history = new SolicitationHistory(
                solicitation,
                HistoryAction.CANCELADO,
                "Não preciso mais");

        assertThat(history.getSolicitation()).isEqualTo(solicitation);
        assertThat(history.getAction()).isEqualTo(HistoryAction.CANCELADO);
        assertThat(history.getReason()).isEqualTo("Não preciso mais");
        assertThat(history.getDate()).isNotNull();
    }

    @Test
    @DisplayName("Equals e hashCode devem considerar apenas o id")
    void deveTestarEqualsEHashCode() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        SolicitationHistory h2 = new SolicitationHistory();
        h2.setId(1L);

        SolicitationHistory h3 = new SolicitationHistory();
        h3.setId(2L);

        assertThat(h1).isEqualTo(h2);
        assertThat(h1).isNotEqualTo(h3);
        assertThat(h1.hashCode()).isEqualTo(h2.hashCode());
    }

    @Test
    @DisplayName("Equals deve retornar false quando id é nulo em um dos objetos")
    void deveTestarEqualsComIdNulo() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(null);

        SolicitationHistory h2 = new SolicitationHistory();
        h2.setId(1L);

        assertThat(h1).isNotEqualTo(h2);
    }

    @Test
    @DisplayName("Equals deve retornar true quando comparar o mesmo objeto")
    void equalsMesmoObjeto() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        assertThat(h1.equals(h1)).isTrue();
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com null")
    void equalsComNull() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        assertThat(h1.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Equals deve retornar false quando id é null em um objeto e não null no outro")
    void equalsIdNullVsNaoNull() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(null);

        SolicitationHistory h2 = new SolicitationHistory();
        h2.setId(1L);

        assertThat(h1.equals(h2)).isFalse();
    }

    @Test
    @DisplayName("Equals deve retornar false quando ids diferentes")
    void equalsIdsDiferentes() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        SolicitationHistory h2 = new SolicitationHistory();
        h2.setId(2L);

        assertThat(h1.equals(h2)).isFalse();
    }

    @Test
    @DisplayName("Equals deve retornar true quando ids iguais")
    void equalsIdsIguais() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        SolicitationHistory h2 = new SolicitationHistory();
        h2.setId(1L);

        assertThat(h1.equals(h2)).isTrue();
    }

    @Test
    @DisplayName("HashCode deve ser 31 quando id é null")
    void hashCodeIdNull() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(null);

        assertThat(h1.hashCode()).isEqualTo(31);
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com classe diferente")
    void equalsClasseDiferente() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        assertThat(h1.equals(new Object())).isFalse(); // evita warning
    }

    @Test
    @DisplayName("HashCode deve ser baseado no id quando não é null")
    void hashCodeIdNaoNull() {
        SolicitationHistory h1 = new SolicitationHistory();
        h1.setId(1L);

        // Implementação retorna 31*1 + id.hashCode() = 32
        assertThat(h1.hashCode()).isEqualTo(32);
    }

}
