package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
        AccessSolicitationRequest dto = new AccessSolicitationRequest(List.of(1L),
                "Justificativa detalhada válida para acesso ao módulo", true);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoListaDeModulosNula() {
        AccessSolicitationRequest dto = new AccessSolicitationRequest(null,
                "Justificativa válida com mais de vinte caracteres", false);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Os módulos são obrigatórios", violations.iterator().next().getMessage());
    }

    @Test
    void deveFalharQuandoListaDeModulosVazia() {
        AccessSolicitationRequest dto = new AccessSolicitationRequest(List.of(),
                "Justificativa válida com mais de vinte caracteres", false);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("É necessário selecionar entre 1 e 3 módulos", violations.iterator().next().getMessage());
    }

    @Test
    void deveFalharQuandoJustificativaVazia() {
        AccessSolicitationRequest dto = new AccessSolicitationRequest(List.of(1L), "", true);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        String msg = violations.iterator().next().getMessage();
        assertTrue(
                msg.equals("A justificativa não pode estar vazia") ||
                        msg.equals("A justificativa deve ter entre 20 e 500 caracteres"),
                "Mensagem inesperada: " + msg);
    }

    @Test
    void deveFalharQuandoJustificativaCurta() {
        AccessSolicitationRequest dto = new AccessSolicitationRequest(List.of(1L), "curta", true);

        Set<ConstraintViolation<AccessSolicitationRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("A justificativa deve ter entre 20 e 500 caracteres", violations.iterator().next().getMessage());
    }
}
