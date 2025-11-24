package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

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

    @Test
    @DisplayName("Deve lançar exceção ao extrair role inválida")
    void shouldThrowExceptionForInvalidRole() {
        String token = jwtUtil.generateToken("rodrigo@empresa.com", "INVALID_ROLE", 1L);

        assertThrows(IllegalArgumentException.class, () -> jwtUtil.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Deve retornar null quando claim userId não existe")
    void shouldReturnNullForMissingUserId() {
        // Gera token sem userId
        String token = Jwts.builder()
                .setSubject("rodrigo@empresa.com")
                .claim("role", Role.ADMIN.name())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor("mySecretKeyWith32CharsMinimumLength123".getBytes()))
                .compact();

        assertNull(jwtUtil.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Deve gerar e validar token válido")
    void shouldGenerateAndValidateValidToken() {
        String token = jwtUtil.generateToken("user@empresa.com", "TI", 1L);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("user@empresa.com", jwtUtil.extractUsername(token));
        assertEquals("user@empresa.com", jwtUtil.getEmailFromToken(token));
        assertEquals(Role.TI, jwtUtil.getRoleFromToken(token));
        assertEquals(1L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Deve retornar false para token expirado")
    void shouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil("12345678901234567890123456789012", 1); // expira em 1ms
        String token = shortLivedJwt.generateToken("user@empresa.com", "TI", 1L);
        Thread.sleep(5); // garante expiração
        assertFalse(shortLivedJwt.validateToken(token));
    }

    @Test
    @DisplayName("Deve retornar false para token inválido")
    void shouldReturnFalseForInvalidToken() {
        String fakeToken = "invalid.token.value";
        assertFalse(jwtUtil.validateToken(fakeToken));
    }

    @Test
    @DisplayName("Deve lançar exceção ao extrair claims de token inválido")
    void shouldThrowExceptionForInvalidTokenClaims() {
        String fakeToken = "invalid.token.value";
        assertThrows(Exception.class, () -> jwtUtil.extractAllClaims(fakeToken));
    }

}
