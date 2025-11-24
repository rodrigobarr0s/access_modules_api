package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;

class RoleTest {

    @Test
    @DisplayName("fromCode deve retornar enum correto para código válido")
    void fromCodeShouldReturnCorrectEnum() {
        assertEquals(Role.ADMIN, Role.fromCode(1));
        assertEquals(Role.FINANCEIRO, Role.fromCode(2));
        assertEquals(Role.RH, Role.fromCode(3));
        assertEquals(Role.OPERACOES, Role.fromCode(4));
        assertEquals(Role.TI, Role.fromCode(5));
        assertEquals(Role.AUDITOR, Role.fromCode(6));
    }

    @Test
    @DisplayName("fromCode deve lançar exceção para código inválido")
    void fromCodeShouldThrowExceptionForInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> Role.fromCode(99));
    }

    @Test
    @DisplayName("getCode deve retornar o código inteiro associado ao enum")
    void getCodeShouldReturnCorrectValue() {
        assertEquals(1, Role.ADMIN.getCode());
        assertEquals(2, Role.FINANCEIRO.getCode());
    }
}
