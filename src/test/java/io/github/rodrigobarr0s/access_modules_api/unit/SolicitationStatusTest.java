package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;

class SolicitationStatusTest {

    @Test
    @DisplayName("valueOf deve retornar enum correto para código válido")
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(SolicitationStatus.ATIVO, SolicitationStatus.valueOf(1));
        assertEquals(SolicitationStatus.NEGADO, SolicitationStatus.valueOf(2));
        assertEquals(SolicitationStatus.CANCELADO, SolicitationStatus.valueOf(3));
    }

    @Test
    @DisplayName("valueOf deve lançar exceção para código inválido")
    void valueOfShouldThrowExceptionForInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> SolicitationStatus.valueOf(99));
    }

    @Test
    @DisplayName("getCode deve retornar o código inteiro associado ao enum")
    void getCodeShouldReturnCorrectValue() {
        assertEquals(1, SolicitationStatus.ATIVO.getCode());
        assertEquals(2, SolicitationStatus.NEGADO.getCode());
        assertEquals(3, SolicitationStatus.CANCELADO.getCode());
    }
}
