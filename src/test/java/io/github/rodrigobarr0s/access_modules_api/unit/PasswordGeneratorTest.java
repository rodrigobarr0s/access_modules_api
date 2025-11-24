package io.github.rodrigobarr0s.access_modules_api.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    @Test
    @DisplayName("Deve gerar hash válido para senha admin")
    void deveGerarHashValidoParaSenhaAdmin() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("admin");

        // Verifica se o hash corresponde à senha original
        assertTrue(encoder.matches("admin", hash));
    }

    @Test
    @DisplayName("Hashes gerados para mesma senha devem ser diferentes (salt aleatório)")
    void deveGerarHashesDiferentesParaMesmaSenha() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash1 = encoder.encode("admin");
        String hash2 = encoder.encode("admin");

        // Mesmo que os hashes sejam diferentes, ambos devem validar a senha
        assertTrue(encoder.matches("admin", hash1));
        assertTrue(encoder.matches("admin", hash2));
        // Normalmente os hashes não são iguais
        assertTrue(!hash1.equals(hash2));
    }
}
