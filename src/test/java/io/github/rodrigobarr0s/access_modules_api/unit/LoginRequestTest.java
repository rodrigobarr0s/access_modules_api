package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.dto.LoginRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void deveCriarLoginRequestComValoresCorretos() {
        LoginRequest request = new LoginRequest("user@test.com", "123456");

        assertEquals("user@test.com", request.email());
        assertEquals("123456", request.password());
    }

    @Test
    void devePermitirValoresNulos() {
        LoginRequest request = new LoginRequest(null, null);

        assertNull(request.email());
        assertNull(request.password());
    }

    @Test
    void deveCompararRecordsCorretamente() {
        LoginRequest r1 = new LoginRequest("user@test.com", "123456");
        LoginRequest r2 = new LoginRequest("user@test.com", "123456");

        assertEquals(r1, r2); // records implementam equals/hashCode automaticamente
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void deveGerarToStringCorretamente() {
        LoginRequest request = new LoginRequest("user@test.com", "123456");

        String toString = request.toString();
        assertTrue(toString.contains("user@test.com"));
        assertTrue(toString.contains("123456"));
    }
}
