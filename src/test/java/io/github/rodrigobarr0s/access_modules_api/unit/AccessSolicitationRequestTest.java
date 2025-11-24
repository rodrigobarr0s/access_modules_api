package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AccessSolicitationRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarValidacaoQuandoDadosSaoValidos() {
        AccessSolicitationRequest dto =
                new AccessSolicitationRequest(1L, "Preciso de acesso urgente", true);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoModuleIdNulo() {
        AccessSolicitationRequest dto =
                new AccessSolicitationRequest(null, "Justificativa válida", false);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("O módulo é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void deveFalharQuandoJustificativaVazia() {
        AccessSolicitationRequest dto =
                new AccessSolicitationRequest(1L, "", true);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("A justificativa não pode estar vazia", violations.iterator().next().getMessage());
    }
}
