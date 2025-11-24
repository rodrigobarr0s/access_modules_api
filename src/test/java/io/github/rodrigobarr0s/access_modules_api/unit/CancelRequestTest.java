package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.dto.CancelRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CancelRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarValidacaoQuandoReasonValido() {
        CancelRequest dto = new CancelRequest("Motivo válido");

        Set<ConstraintViolation<CancelRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Não deve haver violações quando reason é válido");
        assertEquals("Motivo válido", dto.getReason());
    }

    @Test
    void deveFalharQuandoReasonVazio() {
        CancelRequest dto = new CancelRequest("");

        Set<ConstraintViolation<CancelRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Deve haver violações quando reason está vazio");
        assertEquals("O motivo do cancelamento é obrigatório", 
                     violations.iterator().next().getMessage());
    }

    @Test
    void deveFalharQuandoReasonNulo() {
        CancelRequest dto = new CancelRequest(null);

        Set<ConstraintViolation<CancelRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Deve haver violações quando reason é nulo");
        assertEquals("O motivo do cancelamento é obrigatório", 
                     violations.iterator().next().getMessage());
    }
}
