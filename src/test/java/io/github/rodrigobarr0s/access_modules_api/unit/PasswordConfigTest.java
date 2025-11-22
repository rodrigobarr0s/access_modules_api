package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.rodrigobarr0s.access_modules_api.config.PasswordConfig;

class PasswordConfigTest {

    private final PasswordConfig config = new PasswordConfig();

    @Test
    @DisplayName("PasswordEncoder deve codificar e validar senha corretamente")
    void passwordEncoder_shouldEncodeAndMatch() {
        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword, "Senha codificada não deve ser igual à original");
        assertTrue(encoder.matches(rawPassword, encodedPassword), "Senha codificada deve validar corretamente");
    }
}
