package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.controller.exception.GlobalExceptionHandler;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleNotFound deve retornar 404 e mensagem da exceção")
    void handleNotFoundShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Usuário", "id=1");
        ResponseEntity<String> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Usuário"));
    }

    @Test
    @DisplayName("handleDuplicate deve retornar 409 e mensagem da exceção")
    void handleDuplicateShouldReturn409() {
        DuplicateEntityException ex = new DuplicateEntityException("Usuário", "email=teste@empresa.com");
        ResponseEntity<String> response = handler.handleDuplicate(ex);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Usuário"));
    }

    @Test
    @DisplayName("handleAccessDenied deve retornar 403 e mensagem da exceção")
    void handleAccessDeniedShouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("Acesso negado");
        ResponseEntity<String> response = handler.handleAccessDenied(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Acesso negado", response.getBody());
    }
}
