package io.github.rodrigobarr0s.access_modules_api.unit;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuleTest {

    @Test
    @DisplayName("Deve construir Module com construtor vazio")
    void deveConstruirComConstrutorVazio() {
        Module module = new Module();
        assertNull(module.getId());
        assertNull(module.getName());
        assertNull(module.getDescription());
        assertTrue(module.getAccesses().isEmpty());
        assertTrue(module.getIncompatibilities().isEmpty());
    }

    @Test
    @DisplayName("Deve construir Module com construtor completo")
    void deveConstruirComConstrutorCompleto() {
        Module module = new Module(1L, "Financeiro", "Módulo de gestão financeira");
        assertEquals(1L, module.getId());
        assertEquals("Financeiro", module.getName());
        assertEquals("Módulo de gestão financeira", module.getDescription());
    }

    @Test
    @DisplayName("Deve construir Module com construtor parcial")
    void deveConstruirComConstrutorParcial() {
        Module module = new Module("RH", "Módulo de recursos humanos");
        assertNull(module.getId());
        assertEquals("RH", module.getName());
        assertEquals("Módulo de recursos humanos", module.getDescription());
    }

    @Test
    @DisplayName("Deve adicionar e remover UserModuleAccess corretamente")
    void deveAdicionarERemoverAccess() {
        Module module = new Module("Financeiro", "desc");
        UserModuleAccess access = new UserModuleAccess();

        module.addAccess(access);
        assertTrue(module.getAccesses().contains(access));
        assertEquals(module, access.getModule());

        module.removeAccess(access);
        assertFalse(module.getAccesses().contains(access));
        assertNull(access.getModule());
    }

    @Test
    @DisplayName("Deve adicionar e remover ModuleIncompatibility corretamente")
    void deveAdicionarERemoverIncompatibility() {
        Module module = new Module("Financeiro", "desc");
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();

        module.addIncompatibility(incompatibility);
        assertTrue(module.getIncompatibilities().contains(incompatibility));
        assertEquals(module, incompatibility.getModule());

        module.removeIncompatibility(incompatibility);
        assertFalse(module.getIncompatibilities().contains(incompatibility));
        assertNull(incompatibility.getModule());
    }

    @Test
    @DisplayName("Deve validar equals e hashCode baseados em id")
    void deveValidarEqualsEHashCode() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(1L, "Financeiro", "desc");
        Module m3 = new Module(2L, "RH", "desc");

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString contendo id e nome")
    void deveGerarToStringCorretamente() {
        Module module = new Module(1L, "Financeiro", "desc");
        String toString = module.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("Financeiro"));
    }
}
