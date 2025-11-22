package io.github.rodrigobarr0s.access_modules_api.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller criado exclusivamente para fins de teste de integração.
 * 
 * Este endpoint é protegido pela configuração de segurança (SecurityConfig)
 * e serve para validar o fluxo completo de autenticação JWT.
 *
 * Só é carregado quando o profile ativo é "test".
 */
@RestController
@RequestMapping("/api/protected")
@Profile("test")
public class ProtectedController {

    @GetMapping
    public String securedEndpoint() {
        return "Acesso liberado com JWT válido!";
    }
}
