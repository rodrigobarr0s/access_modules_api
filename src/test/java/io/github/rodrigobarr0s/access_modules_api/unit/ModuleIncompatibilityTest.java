package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuleIncompatibilityTest {

    @Test
    @DisplayName("Deve construir ModuleIncompatibility com construtor vazio")
    void deveConstruirComConstrutorVazio() {
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();
        assertNull(incompatibility.getId());
        assertNull(incompatibility.getModule());
        assertNull(incompatibility.getIncompatibleModule());
    }

    @Test
    @DisplayName("Deve construir ModuleIncompatibility com construtor completo")
    void deveConstruirComConstrutorCompleto() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(10L, m1, m2);

        assertEquals(10L, incompatibility.getId());
        assertEquals(m1, incompatibility.getModule());
        assertEquals(m2, incompatibility.getIncompatibleModule());
    }

    @Test
    @DisplayName("Deve construir ModuleIncompatibility com construtor parcial")
    void deveConstruirComConstrutorParcial() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m2);

        assertNull(incompatibility.getId());
        assertEquals(m1, incompatibility.getModule());
        assertEquals(m2, incompatibility.getIncompatibleModule());
    }

    @Test
    @DisplayName("Deve lançar exceção quando módulo for igual ao incompatível")
    void deveLancarExcecaoQuandoModuloIgual() {
        Module m1 = new Module(1L, "Financeiro", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, incompatibility::validate);
        assertEquals("Um módulo não pode ser incompatível consigo mesmo.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar equals e hashCode baseados em id")
    void deveValidarEqualsEHashCode() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility i1 = new ModuleIncompatibility(10L, m1, m2);
        ModuleIncompatibility i2 = new ModuleIncompatibility(10L, m1, m2);
        ModuleIncompatibility i3 = new ModuleIncompatibility(20L, m1, m2);

        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
        assertEquals(i1.hashCode(), i2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString contendo id e nomes dos módulos")
    void deveGerarToStringCorretamente() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(10L, m1, m2);

        String toString = incompatibility.toString();
        assertTrue(toString.contains("id=10"));
        assertTrue(toString.contains("Financeiro"));
        assertTrue(toString.contains("RH"));
    }
}
