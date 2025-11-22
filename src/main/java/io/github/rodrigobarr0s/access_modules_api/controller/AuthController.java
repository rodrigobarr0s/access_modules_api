package io.github.rodrigobarr0s.access_modules_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.rodrigobarr0s.access_modules_api.dto.LoginRequest;
import io.github.rodrigobarr0s.access_modules_api.dto.LoginResponse;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = service.findByEmail(request.email());

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole().name()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

}
