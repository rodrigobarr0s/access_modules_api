package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.dto.LoginResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void deveCriarLoginResponseComValoresCorretos() {
        LoginResponse response = new LoginResponse("token123", "user@test.com", "ADMIN");

        assertEquals("token123", response.token());
        assertEquals("user@test.com", response.email());
        assertEquals("ADMIN", response.role());
    }

    @Test
    void devePermitirValoresNulos() {
        LoginResponse response = new LoginResponse(null, null, null);

        assertNull(response.token());
        assertNull(response.email());
        assertNull(response.role());
    }

    @Test
    void deveCompararRecordsCorretamente() {
        LoginResponse r1 = new LoginResponse("token123", "user@test.com", "ADMIN");
        LoginResponse r2 = new LoginResponse("token123", "user@test.com", "ADMIN");

        assertEquals(r1, r2); // records implementam equals/hashCode automaticamente
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void deveGerarToStringCorretamente() {
        LoginResponse response = new LoginResponse("token123", "user@test.com", "ADMIN");

        String toString = response.toString();
        assertTrue(toString.contains("token123"));
        assertTrue(toString.contains("user@test.com"));
        assertTrue(toString.contains("ADMIN"));
    }
}
