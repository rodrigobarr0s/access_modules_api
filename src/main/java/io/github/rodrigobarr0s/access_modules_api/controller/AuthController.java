package io.github.rodrigobarr0s.access_modules_api.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // @PostMapping("/login")
    // public String login(@RequestParam String username, @RequestParam String password) {
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    //     if (passwordEncoder.matches(password, user.getPassword())) {
    //         return jwtUtil.generateToken(username);
    //     }
    //     throw new RuntimeException("Credenciais inválidas");
    // }

}
