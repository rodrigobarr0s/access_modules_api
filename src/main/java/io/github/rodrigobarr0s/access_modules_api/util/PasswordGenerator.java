package io.github.rodrigobarr0s.access_modules_api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("finance_admin: " + encoder.encode("admin"));
        System.out.println("rh_admin: " + encoder.encode("admin"));
        System.out.println("ops_user: " + encoder.encode("admin"));
        System.out.println("ti_auditor: " + encoder.encode("admin"));
    }
}

