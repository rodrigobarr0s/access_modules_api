package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;

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

    @Test
    @DisplayName("equals deve retornar true para mesmo objeto")
    void equalsShouldReturnTrueForSameObject() {
        Module module = new Module(1L, "Financeiro", "Gestão Financeira");
        assertTrue(module.equals(module));
    }

    @Test
    @DisplayName("equals deve retornar false para classe diferente")
    void equalsShouldReturnFalseForDifferentClass() {
        Module module = new Module(1L, "Financeiro", "Gestão Financeira");
        assertFalse(module.equals("string"));
    }

    @Test
    @DisplayName("equals deve retornar true para IDs iguais")
    void equalsShouldReturnTrueForSameId() {
        Module m1 = new Module(1L, "Financeiro", "Gestão Financeira");
        Module m2 = new Module(1L, "Outro", "Outro módulo");
        assertTrue(m1.equals(m2));
    }

    @Test
    @DisplayName("equals deve retornar false para IDs diferentes")
    void equalsShouldReturnFalseForDifferentId() {
        Module m1 = new Module(1L, "Financeiro", "Gestão Financeira");
        Module m2 = new Module(2L, "Outro", "Outro módulo");
        assertFalse(m1.equals(m2));
    }

    @Test
    @DisplayName("addAccess e removeAccess devem manter consistência")
    void addAndRemoveAccessShouldMaintainConsistency() {
        Module module = new Module(1L, "Financeiro", "Gestão Financeira");
        User user = new User("user@empresa.com", "123", null);
        UserModuleAccess access = new UserModuleAccess(user, module);

        module.addAccess(access);
        assertTrue(module.getAccesses().contains(access));
        assertEquals(module, access.getModule());

        module.removeAccess(access);
        assertFalse(module.getAccesses().contains(access));
        assertNull(access.getModule());
    }

    @Test
    @DisplayName("addIncompatibility e removeIncompatibility devem manter consistência")
    void addAndRemoveIncompatibilityShouldMaintainConsistency() {
        Module module = new Module(1L, "Financeiro", "Gestão Financeira");
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();
        incompatibility.setModule(module);

        module.addIncompatibility(incompatibility);
        assertTrue(module.getIncompatibilities().contains(incompatibility));
        assertEquals(module, incompatibility.getModule());

        module.removeIncompatibility(incompatibility);
        assertFalse(module.getIncompatibilities().contains(incompatibility));
        assertNull(incompatibility.getModule());
    }

    @Test
    @DisplayName("toString deve retornar representação legível")
    void toStringShouldReturnReadableRepresentation() {
        Module module = new Module(1L, "Financeiro", "Gestão Financeira");
        String result = module.toString();
        assertTrue(result.contains("Financeiro"));
        assertTrue(result.contains("1"));
    }
}
