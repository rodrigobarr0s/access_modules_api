package io.github.rodrigobarr0s.access_modules_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService service;

    public AuthController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserService service) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        User user = service.findByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.ok(jwtUtil.generateToken(username));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

}
