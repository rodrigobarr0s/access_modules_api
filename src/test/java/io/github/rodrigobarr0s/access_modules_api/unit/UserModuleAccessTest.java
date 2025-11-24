package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserModuleAccessTest {

    @Test
    @DisplayName("Deve construir UserModuleAccess com construtor vazio")
    void deveConstruirComConstrutorVazio() {
        UserModuleAccess access = new UserModuleAccess();
        assertNull(access.getId());
        assertNull(access.getUser());
        assertNull(access.getModule());
        assertNotNull(access.getGrantedAt()); // inicializado com LocalDateTime.now()
    }

    @Test
    @DisplayName("Deve construir UserModuleAccess com construtor completo")
    void deveConstruirComConstrutorCompleto() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");

        UserModuleAccess access = new UserModuleAccess(user, module);

        assertEquals(user, access.getUser());
        assertEquals(module, access.getModule());
        assertNotNull(access.getGrantedAt());
    }

    @Test
    @DisplayName("Deve permitir alteração de atributos via setters")
    void devePermitirAlteracaoViaSetters() {
        UserModuleAccess access = new UserModuleAccess();
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");
        LocalDateTime now = LocalDateTime.now();

        access.setId(10L);
        access.setUser(user);
        access.setModule(module);
        access.setGrantedAt(now);

        assertEquals(10L, access.getId());
        assertEquals(user, access.getUser());
        assertEquals(module, access.getModule());
        assertEquals(now, access.getGrantedAt());
    }

    @Test
    @DisplayName("Deve validar equals e hashCode baseados em id")
    void deveValidarEqualsEHashCode() {
        UserModuleAccess a1 = new UserModuleAccess();
        a1.setId(1L);

        UserModuleAccess a2 = new UserModuleAccess();
        a2.setId(1L);

        UserModuleAccess a3 = new UserModuleAccess();
        a3.setId(2L);

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString contendo id, email do usuário e nome do módulo")
    void deveGerarToStringCorretamente() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);
        Module module = new Module(2L, "Financeiro", "desc");

        UserModuleAccess access = new UserModuleAccess(user, module);
        access.setId(10L);

        String toString = access.toString();
        assertTrue(toString.contains("id=10"));
        assertTrue(toString.contains("user@test.com"));
        assertTrue(toString.contains("Financeiro"));
    }

    @Test
    @DisplayName("Equals deve retornar true quando comparar o mesmo objeto")
    void equalsShouldReturnTrueForSameObject() {
        UserModuleAccess access = new UserModuleAccess();
        assertTrue(access.equals(access));
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com classe diferente")
    void equalsShouldReturnFalseForDifferentClass() {
        UserModuleAccess access = new UserModuleAccess();
        assertFalse(access.equals("string"));
    }

    @Test
    @DisplayName("Equals deve retornar true quando IDs são iguais")
    void equalsShouldReturnTrueForSameId() {
        UserModuleAccess a1 = new UserModuleAccess();
        a1.setId(1L);
        UserModuleAccess a2 = new UserModuleAccess();
        a2.setId(1L);
        assertTrue(a1.equals(a2));
    }

    @Test
    @DisplayName("Equals deve retornar false quando IDs são diferentes")
    void equalsShouldReturnFalseForDifferentIds() {
        UserModuleAccess a1 = new UserModuleAccess();
        a1.setId(1L);
        UserModuleAccess a2 = new UserModuleAccess();
        a2.setId(2L);
        assertFalse(a1.equals(a2));
    }

    @Test
    @DisplayName("ToString deve lidar com user e module nulos")
    void toStringShouldHandleNullUserAndModule() {
        UserModuleAccess access = new UserModuleAccess();
        String result = access.toString();
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("ToString deve incluir email e nome do módulo quando definidos")
    void toStringShouldIncludeUserAndModule() {
        User user = new User();
        user.setEmail("teste@empresa.com");

        Module module = new Module();
        module.setName("Financeiro");

        UserModuleAccess access = new UserModuleAccess(user, module);
        String result = access.toString();

        assertTrue(result.contains("teste@empresa.com"));
        assertTrue(result.contains("Financeiro"));
    }
}
