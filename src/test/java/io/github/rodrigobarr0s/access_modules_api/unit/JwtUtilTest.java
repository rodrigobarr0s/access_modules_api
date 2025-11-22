package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "mySecretKeyWith32CharsMinimumLength123", // secret fixo para teste
            60000L // 60 segundos
    );

    @Test
    @DisplayName("Deve gerar token JWT válido com claims corretos")
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("rodrigo@empresa.com", jwtUtil.getEmailFromToken(token));
        assertEquals(Role.ADMIN, jwtUtil.getRoleFromToken(token));
        assertEquals(1L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Deve invalidar token expirado")
    void shouldInvalidateExpiredToken() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil("mySecretKeyWith32CharsMinimumLength123", 1);
        String token = shortLivedJwt.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        Thread.sleep(5); // espera expirar

        assertFalse(shortLivedJwt.validateToken(token));
    }

    @Test
    @DisplayName("Deve invalidar token malformado")
    void shouldInvalidateMalformedToken() {
        String invalidToken = "abc.def.ghi";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }
}
