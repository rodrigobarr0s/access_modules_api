package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccessSolicitationTest {

    @Test
    @DisplayName("Deve construir AccessSolicitation com construtor completo")
    void deveConstruirComConstrutorCompleto() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");
        LocalDateTime now = LocalDateTime.now();

        AccessSolicitation solicitation = new AccessSolicitation(
                10L, user, module, "PROTO123",
                SolicitationStatus.ATIVO, "Preciso de acesso",
                true, now, now, now.plusDays(1),
                "cancelado", "negado");

        assertEquals(10L, solicitation.getId());
        assertEquals("PROTO123", solicitation.getProtocolo());
        assertEquals(SolicitationStatus.ATIVO, solicitation.getStatus());
        assertEquals("Preciso de acesso", solicitation.getJustificativa());
        assertTrue(solicitation.isUrgente());
        assertEquals(user, solicitation.getUser());
        assertEquals(module, solicitation.getModule());
        assertEquals("cancelado", solicitation.getCancelReason());
        assertEquals("negado", solicitation.getNegationReason());
    }

    @Test
    @DisplayName("Deve executar @PrePersist e inicializar datas corretamente")
    void deveExecutarPrePersist() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();

        assertNotNull(solicitation.getCreatedAt());
        assertNotNull(solicitation.getUpdatedAt());
        assertEquals(solicitation.getCreatedAt(), solicitation.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve executar @PreUpdate e atualizar updatedAt")
    void deveExecutarPreUpdate() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();
        LocalDateTime created = solicitation.getCreatedAt();

        solicitation.preUpdate();

        assertTrue(
                solicitation.getUpdatedAt().isAfter(created) || solicitation.getUpdatedAt().isEqual(created),
                "updatedAt deve ser igual ou posterior a createdAt");
    }

    @Test
    @DisplayName("Deve validar equals e hashCode baseados em id")
    void deveTestarEqualsEHashCode() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setId(1L);

        AccessSolicitation s2 = new AccessSolicitation();
        s2.setId(1L);

        AccessSolicitation s3 = new AccessSolicitation();
        s3.setId(2L);

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString contendo id e protocolo")
    void deveGerarToStringCorretamente() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setId(1L);
        solicitation.setProtocolo("PROTO123");

        String toString = solicitation.toString();
        assertTrue(toString.contains("PROTO123"));
        assertTrue(toString.contains("id=1"));
    }

    @Test
    @DisplayName("Deve converter status corretamente para enum SolicitationStatus")
    void deveConverterStatusCorretamente() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(SolicitationStatus.NEGADO);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando status não estiver definido")
    void deveRetornarNullQuandoStatusNaoDefinido() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(null);

        assertNull(solicitation.getStatus());
    }

    @Test
    @DisplayName("getStatus deve retornar null quando status não está definido")
    void getStatusShouldReturnNull() {
        AccessSolicitation solicitation = new AccessSolicitation();
        assertNull(solicitation.getStatus());
    }

    @Test
    @DisplayName("setStatus deve atribuir código corretamente")
    void setStatusShouldAssignCode() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(SolicitationStatus.ATIVO);
        assertEquals(SolicitationStatus.ATIVO, solicitation.getStatus());
    }

    @Test
    @DisplayName("prePersist deve inicializar createdAt e updatedAt")
    void prePersistShouldInitializeDates() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();
        assertNotNull(solicitation.getCreatedAt());
        assertNotNull(solicitation.getUpdatedAt());
    }

    @Test
    @DisplayName("preUpdate deve atualizar updatedAt")
    void preUpdateShouldUpdateDate() throws InterruptedException {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();
        LocalDateTime before = solicitation.getUpdatedAt();

        Thread.sleep(5); // garante diferença mínima
        solicitation.preUpdate();
        LocalDateTime after = solicitation.getUpdatedAt();

        assertTrue(after.isAfter(before));
    }

    @Test
    @DisplayName("equals deve retornar true para mesmo objeto")
    void equalsShouldReturnTrueForSameObject() {
        AccessSolicitation solicitation = new AccessSolicitation();
        assertTrue(solicitation.equals(solicitation));
    }

    @Test
    @DisplayName("equals deve retornar false para classe diferente")
    void equalsShouldReturnFalseForDifferentClass() {
        AccessSolicitation solicitation = new AccessSolicitation();
        assertFalse(solicitation.equals(new Object()));
    }

    @Test
    @DisplayName("equals deve retornar false para IDs diferentes")
    void equalsShouldReturnFalseForDifferentIds() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setId(1L);
        AccessSolicitation s2 = new AccessSolicitation();
        s2.setId(2L);
        assertFalse(s1.equals(s2));
    }

    @Test
    @DisplayName("toString deve lidar com user e module nulos")
    void toStringShouldHandleNullUserAndModule() {
        AccessSolicitation solicitation = new AccessSolicitation();
        String result = solicitation.toString();
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("setStatus deve ignorar quando status é null")
    void setStatusShouldIgnoreNull() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(null);
        assertNull(solicitation.getStatus());
    }

}
