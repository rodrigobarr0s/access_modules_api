package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Deve construir User com construtor vazio")
    void deveConstruirComConstrutorVazio() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertTrue(user.getAccesses().isEmpty());
    }

    @Test
    @DisplayName("Deve construir User com construtor completo")
    void deveConstruirComConstrutorCompleto() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);

        assertEquals(1L, user.getId());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("123456", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Deve construir User com construtor parcial")
    void deveConstruirComConstrutorParcial() {
        User user = new User("user@test.com", "abcdef", Role.FINANCEIRO);

        assertNull(user.getId());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("abcdef", user.getPassword());
        assertEquals(Role.FINANCEIRO, user.getRole());
    }

    @Test
    @DisplayName("Deve adicionar e remover UserModuleAccess corretamente")
    void deveAdicionarERemoverAccess() {
        User user = new User("user@test.com", "abcdef", Role.FINANCEIRO);
        UserModuleAccess access = new UserModuleAccess();

        user.addAccess(access);
        assertTrue(user.getAccesses().contains(access));
        assertEquals(user, access.getUser());

        user.removeAccess(access);
        assertFalse(user.getAccesses().contains(access));
        assertNull(access.getUser());
    }

    @Test
    @DisplayName("Deve validar equals e hashCode baseados em id")
    void deveValidarEqualsEHashCode() {
        User u1 = new User(1L, "user@test.com", "123456", Role.ADMIN);
        User u2 = new User(1L, "user@test.com", "123456", Role.ADMIN);
        User u3 = new User(2L, "other@test.com", "abcdef", Role.RH);

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString contendo id, email e role")
    void deveGerarToStringCorretamente() {
        User user = new User(1L, "user@test.com", "123456", Role.ADMIN);

        String toString = user.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("user@test.com"));
        assertTrue(toString.contains("ADMIN"));
    }
}
