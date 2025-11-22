package io.github.rodrigobarr0s.access_modules_api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("financeiro@empresa.com: " + encoder.encode("admin"));
        System.out.println("rh@empresa.com: " + encoder.encode("admin"));
        System.out.println("operacoes@empresa.com: " + encoder.encode("admin"));
        System.out.println("auditoria@empresa.com: " + encoder.encode("admin"));
    }
}

