package io.github.rodrigobarr0s.access_modules_api.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;

class JwtUtilValidTokenTest {

    @Test
    @DisplayName("Deve validar token JWT válido usando secret configurado")
    void shouldValidateValidToken() {
        // Usa o mesmo secret definido em application.properties
        String secret = "mysupersecretkeymysupersecretkeymysupersecretkey";
        long expiration = 900000L; // 15 minutos

        JwtUtil jwtUtil = new JwtUtil(secret, expiration);

        // Gera token válido
        String token = jwtUtil.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        // Aqui o fluxo passa pelo return !claims.getExpiration().before(new Date());
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("rodrigo@empresa.com", jwtUtil.getEmailFromToken(token));
        assertEquals(Role.ADMIN, jwtUtil.getRoleFromToken(token));
        assertEquals(1L, jwtUtil.getUserIdFromToken(token));
    }
}
