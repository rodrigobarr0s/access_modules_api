package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;

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

        Module module = new Module("Financeiro", "desc"); // usa o construtor da sua entidade
        UserModuleAccess access = new UserModuleAccess(user, module); // já associa user+module

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

    @Test
    @DisplayName("Deve retornar null quando role não está definido")
    void getRoleShouldReturnNullWhenRoleIsNull() {
        User user = new User();
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("Deve atribuir role corretamente quando não é null")
    void setRoleShouldAssignCode() {
        User user = new User();
        user.setRole(Role.FINANCEIRO);
        assertEquals(Role.FINANCEIRO, user.getRole());
    }

    @Test
    @DisplayName("Equals deve retornar true quando comparar o mesmo objeto")
    void equalsShouldReturnTrueForSameObject() {
        User user = new User();
        assertTrue(user.equals(user));
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com classe diferente")
    void equalsShouldReturnFalseForDifferentClass() {
        User user = new User();
        assertFalse(user.equals(new Object()));
    }

    @Test
    @DisplayName("Equals deve retornar false quando IDs são diferentes")
    void equalsShouldReturnFalseForDifferentIds() {
        User u1 = new User(1L, "a@a.com", "123456", Role.RH);
        User u2 = new User(2L, "b@b.com", "123456", Role.RH);
        assertFalse(u1.equals(u2));
    }

    @Test
    @DisplayName("ToString deve incluir role quando definido")
    void toStringShouldIncludeRole() {
        User user = new User(1L, "a@a.com", "123456", Role.RH);
        String result = user.toString();
        assertTrue(result.contains("RH"));
    }

    @Test
    @DisplayName("ToString deve lidar com role null")
    void toStringShouldHandleNullRole() {
        User user = new User();
        String result = user.toString();
        assertTrue(result.contains("null"));
    }
}
