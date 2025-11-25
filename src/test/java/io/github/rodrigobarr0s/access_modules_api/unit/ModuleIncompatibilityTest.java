package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuleIncompatibilityTest {

    @Test
    @DisplayName("Construtor vazio deve inicializar atributos como null")
    void deveConstruirComConstrutorVazio() {
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();
        assertAll(
                () -> assertNull(incompatibility.getId()),
                () -> assertNull(incompatibility.getModule()),
                () -> assertNull(incompatibility.getIncompatibleModule())
        );
    }

    @Test
    @DisplayName("Construtor completo deve inicializar todos os atributos")
    void deveConstruirComConstrutorCompleto() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(10L, m1, m2);

        assertAll(
                () -> assertEquals(10L, incompatibility.getId()),
                () -> assertEquals(m1, incompatibility.getModule()),
                () -> assertEquals(m2, incompatibility.getIncompatibleModule())
        );
    }

    @Test
    @DisplayName("Construtor parcial deve inicializar módulos e id como null")
    void deveConstruirComConstrutorParcial() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m2);

        assertAll(
                () -> assertNull(incompatibility.getId()),
                () -> assertEquals(m1, incompatibility.getModule()),
                () -> assertEquals(m2, incompatibility.getIncompatibleModule())
        );
    }

    @Test
    @DisplayName("Validate deve lançar exceção quando módulo for igual ao incompatível")
    void deveLancarExcecaoQuandoModuloIgual() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, incompatibility::validate);
        assertEquals("Um módulo não pode ser incompatível consigo mesmo.", ex.getMessage());
    }

    @Test
    @DisplayName("Validate não deve lançar exceção quando módulos são diferentes ou nulos")
    void validateNaoDeveLancarExcecao() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility i1 = new ModuleIncompatibility(m1, m2);
        ModuleIncompatibility i2 = new ModuleIncompatibility(null, m2);
        ModuleIncompatibility i3 = new ModuleIncompatibility(m1, null);

        assertAll(
                () -> assertDoesNotThrow(i1::validate),
                () -> assertDoesNotThrow(i2::validate),
                () -> assertDoesNotThrow(i3::validate)
        );
    }

    @Test
    @DisplayName("Equals e hashCode devem considerar apenas id")
    void deveValidarEqualsEHashCode() {
        ModuleIncompatibility i1 = new ModuleIncompatibility();
        i1.setId(10L);

        ModuleIncompatibility i2 = new ModuleIncompatibility();
        i2.setId(10L);

        ModuleIncompatibility i3 = new ModuleIncompatibility();
        i3.setId(20L);

        assertAll(
                () -> assertEquals(i1, i2),
                () -> assertNotEquals(i1, i3),
                () -> assertEquals(i1.hashCode(), i2.hashCode()),
                () -> assertNotEquals(i1.hashCode(), i3.hashCode())
        );
    }

    @Test
    @DisplayName("Equals deve retornar false para null e classe diferente")
    void equalsDeveRetornarFalseParaNullOuClasseDiferente() {
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();
        assertFalse(incompatibility.equals(null));
        assertFalse(incompatibility.equals(new Object()));
    }

    @Test
    @DisplayName("ToString deve incluir id e nomes dos módulos quando definidos")
    void deveGerarToStringCorretamente() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(10L, m1, m2);

        String toString = incompatibility.toString();
        assertAll(
                () -> assertTrue(toString.contains("id=10")),
                () -> assertTrue(toString.contains("Financeiro")),
                () -> assertTrue(toString.contains("RH"))
        );
    }

    @Test
    @DisplayName("ToString deve lidar com módulos nulos e parciais")
    void toStringDeveLidarComNulosEParciais() {
        Module m1 = new Module(1L, "Financeiro", "desc");

        ModuleIncompatibility i1 = new ModuleIncompatibility();
        ModuleIncompatibility i2 = new ModuleIncompatibility(m1, null);
        ModuleIncompatibility i3 = new ModuleIncompatibility(null, m1);

        assertAll(
                () -> assertTrue(i1.toString().contains("null")),
                () -> assertTrue(i2.toString().contains("Financeiro")),
                () -> assertTrue(i3.toString().contains("Financeiro"))
        );
    }

    @Test
    @DisplayName("Getters e Setters devem atribuir valores corretamente")
    void deveTestarGettersESetters() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(2L, "RH", "desc");

        ModuleIncompatibility incompatibility = new ModuleIncompatibility();
        incompatibility.setId(99L);
        incompatibility.setModule(m1);
        incompatibility.setIncompatibleModule(m2);

        assertAll(
                () -> assertEquals(99L, incompatibility.getId()),
                () -> assertEquals(m1, incompatibility.getModule()),
                () -> assertEquals(m2, incompatibility.getIncompatibleModule())
        );
    }
}
