package io.github.rodrigobarr0s.access_modules_api.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordGeneratorTest {

    @Test
    void generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("financeiro@empresa.com: " + encoder.encode("admin"));
        System.out.println("rh@empresa.com: " + encoder.encode("admin"));
        System.out.println("operacoes@empresa.com: " + encoder.encode("admin"));
        System.out.println("ti@empresa.com: " + encoder.encode("admin"));
    }
}
