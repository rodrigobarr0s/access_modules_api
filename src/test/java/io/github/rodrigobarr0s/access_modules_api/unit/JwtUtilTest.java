package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String secret = "mySecretKeyWith32CharsMinimumLength123"; // >= 32 chars
    private final long expiration = 60000L; // 60 segundos
    private final JwtUtil jwtUtil = new JwtUtil(secret, expiration);

    @Test
    @DisplayName("Deve gerar token JWT válido com claims corretos")
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        assertAll(
                () -> assertNotNull(token),
                () -> assertTrue(jwtUtil.validateToken(token)),
                () -> assertEquals("rodrigo@empresa.com", jwtUtil.getEmailFromToken(token)),
                () -> assertEquals(Role.ADMIN, jwtUtil.getRoleFromToken(token)),
                () -> assertEquals(1L, jwtUtil.getUserIdFromToken(token))
        );
    }

    @Test
    @DisplayName("validateToken deve retornar false para token já expirado")
    void shouldReturnFalseForAlreadyExpiredToken() {
        // gera token com expiração no passado usando o mesmo secret
        String expiredToken = Jwts.builder()
                .setSubject("user@empresa.com")
                .claim("role", Role.ADMIN.name())
                .claim("userId", 1L)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // já expirado
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        assertFalse(jwtUtil.validateToken(expiredToken));
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
        String token = Jwts.builder()
                .setSubject("rodrigo@empresa.com")
                .claim("role", Role.ADMIN.name())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        assertNull(jwtUtil.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Deve gerar e validar token válido com role TI")
    void shouldGenerateAndValidateValidTokenWithRoleTI() {
        String token = jwtUtil.generateToken("user@empresa.com", Role.TI.name(), 1L);

        assertAll(
                () -> assertTrue(jwtUtil.validateToken(token)),
                () -> assertEquals("user@empresa.com", jwtUtil.extractUsername(token)),
                () -> assertEquals("user@empresa.com", jwtUtil.getEmailFromToken(token)),
                () -> assertEquals(Role.TI, jwtUtil.getRoleFromToken(token)),
                () -> assertEquals(1L, jwtUtil.getUserIdFromToken(token))
        );
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
