package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationResponse;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessSolicitationResponseTest {

    @Test
    void deveConstruirResponseComDadosCompletos() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");
        solicitation.setStatus(SolicitationStatus.ATIVO); // ✅ enum correto
        solicitation.setJustificativa("Preciso de acesso");
        solicitation.setUrgente(true);
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setCancelReason("cancelado");
        solicitation.setNegationReason("negado");

        AccessSolicitationResponse response = new AccessSolicitationResponse(solicitation);

        assertEquals("PROTO123", response.protocolo());
        assertEquals("ATIVO", response.status()); // record chama .name() no enum
        assertEquals("Preciso de acesso", response.justificativa());
        assertTrue(response.urgente());
        assertEquals(1L, response.userId());
        assertEquals("user@test.com", response.userEmail());
        assertEquals(2L, response.moduleId());
        assertEquals("Financeiro", response.moduleName());
        assertEquals("cancelado", response.cancelReason());
        assertEquals("negado", response.negationReason());
    }

    @Test
    void deveConstruirResponseComUsuarioNulo() {
        Module module = new Module(2L, "Financeiro", "desc");

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO456");
        solicitation.setStatus(SolicitationStatus.CANCELADO);
        solicitation.setJustificativa("Sem usuário");
        solicitation.setUrgente(false);
        solicitation.setUser(null);
        solicitation.setModule(module);

        AccessSolicitationResponse response = new AccessSolicitationResponse(solicitation);

        assertEquals("PROTO456", response.protocolo());
        assertEquals("CANCELADO", response.status());
        assertEquals("Sem usuário", response.justificativa());
        assertFalse(response.urgente());
        assertNull(response.userId());
        assertNull(response.userEmail());
        assertEquals(2L, response.moduleId());
        assertEquals("Financeiro", response.moduleName());
    }

    @Test
    void deveConstruirResponseComModuloNulo() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO789");
        solicitation.setStatus(SolicitationStatus.NEGADO);
        solicitation.setJustificativa("Sem módulo");
        solicitation.setUrgente(false);
        solicitation.setUser(user);
        solicitation.setModule(null);

        AccessSolicitationResponse response = new AccessSolicitationResponse(solicitation);

        assertEquals("PROTO789", response.protocolo());
        assertEquals("NEGADO", response.status());
        assertEquals("Sem módulo", response.justificativa());
        assertFalse(response.urgente());
        assertEquals(1L, response.userId());
        assertEquals("user@test.com", response.userEmail());
        assertNull(response.moduleId());
        assertNull(response.moduleName());
    }
}
