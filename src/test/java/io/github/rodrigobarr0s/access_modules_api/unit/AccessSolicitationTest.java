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
    @DisplayName("Construtor completo deve inicializar todos os atributos corretamente")
    void deveConstruirComConstrutorCompleto() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");
        LocalDateTime now = LocalDateTime.now();

        AccessSolicitation solicitation = new AccessSolicitation(
                10L, user, module, "PROTO123",
                SolicitationStatus.ATIVO, "Preciso de acesso",
                true, now, now, now.plusDays(1),
                "cancelado", "negado");

        assertAll(
                () -> assertEquals(10L, solicitation.getId()),
                () -> assertEquals("PROTO123", solicitation.getProtocolo()),
                () -> assertEquals(SolicitationStatus.ATIVO, solicitation.getStatus()),
                () -> assertEquals("Preciso de acesso", solicitation.getJustificativa()),
                () -> assertTrue(solicitation.isUrgente()),
                () -> assertEquals(user, solicitation.getUser()),
                () -> assertEquals(module, solicitation.getModule()),
                () -> assertEquals("cancelado", solicitation.getCancelReason()),
                () -> assertEquals("negado", solicitation.getNegationReason()));
    }

    @Test
    @DisplayName("@PrePersist deve inicializar createdAt e updatedAt iguais")
    void deveExecutarPrePersist() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();

        assertNotNull(solicitation.getCreatedAt());
        assertNotNull(solicitation.getUpdatedAt());
        assertEquals(solicitation.getCreatedAt(), solicitation.getUpdatedAt());
    }

    @Test
    @DisplayName("@PreUpdate deve atualizar updatedAt para valor posterior")
    void deveExecutarPreUpdate() throws InterruptedException {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.prePersist();
        LocalDateTime before = solicitation.getUpdatedAt();

        Thread.sleep(5); // garante diferença mínima
        solicitation.preUpdate();

        assertTrue(solicitation.getUpdatedAt().isAfter(before));
    }

    @Test
    @DisplayName("Equals e hashCode devem considerar apenas id quando definido")
    void deveTestarEqualsEHashCodeComId() {
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
    @DisplayName("Equals deve retornar false quando um objeto tem id e outro não")
    void equalsDeveRetornarFalseQuandoUmTemIdOutroNao() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setId(1L);
        AccessSolicitation s2 = new AccessSolicitation(); // id nulo

        assertNotEquals(s1, s2);
    }

    @Test
    @DisplayName("Equals deve retornar true para mesmo objeto e false para classe diferente")
    void equalsDeveTratarMesmoObjetoEDiferenteClasse() {
        AccessSolicitation solicitation = new AccessSolicitation();
        assertTrue(solicitation.equals(solicitation));
        assertFalse(solicitation.equals(new Object()));
    }

    @Test
    @DisplayName("ToString deve incluir id e protocolo quando definidos")
    void deveGerarToStringCorretamente() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setId(1L);
        solicitation.setProtocolo("PROTO123");

        String toString = solicitation.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("PROTO123"));
    }

    @Test
    @DisplayName("ToString deve lidar com user e module nulos")
    void toStringDeveLidarComUserEModuleNulos() {
        AccessSolicitation solicitation = new AccessSolicitation();
        String result = solicitation.toString();
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("ToString deve incluir apenas user quando module é nulo")
    void toStringDeveIncluirApenasUserQuandoModuleNulo() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);

        String result = solicitation.toString();
        assertTrue(result.contains("user@test.com"));
        assertTrue(result.contains("null")); // módulo nulo
    }

    @Test
    @DisplayName("ToString deve incluir apenas module quando user é nulo")
    void toStringDeveIncluirApenasModuleQuandoUserNulo() {
        Module module = new Module(2L, "Financeiro", "desc");
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setModule(module);

        String result = solicitation.toString();
        assertTrue(result.contains("Financeiro"));
        assertTrue(result.contains("null")); // usuário nulo
    }

}
