package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleIncompatibilityRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;

@DataJpaTest
@ActiveProfiles("test")
class ModuleIncompatibilityRepositoryTest {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleIncompatibilityRepository incompatibilityRepository;

    @Test
    @DisplayName("Deve salvar e buscar incompatibilidades por módulo")
    void deveSalvarEBuscarPorModulo() {
        Module m1 = moduleRepository.save(new Module("Financeiro", "Gestão financeira"));
        Module m2 = moduleRepository.save(new Module("RH", "Recursos Humanos"));

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m2);
        incompatibilityRepository.save(incompatibility);

        List<ModuleIncompatibility> encontrados = incompatibilityRepository.findByModule(m1);

        assertFalse(encontrados.isEmpty());
        assertEquals(m1, encontrados.get(0).getModule());
        assertEquals(m2, encontrados.get(0).getIncompatibleModule());
    }

    @Test
    @DisplayName("Deve buscar incompatibilidade específica por módulo e incompatível")
    void deveBuscarPorModuloEIncompativel() {
        Module m1 = moduleRepository.save(new Module("TI", "Tecnologia da Informação"));
        Module m2 = moduleRepository.save(new Module("Operações", "Gestão operacional"));

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m2);
        incompatibilityRepository.save(incompatibility);

        Optional<ModuleIncompatibility> encontrado = incompatibilityRepository.findByModuleAndIncompatibleModule(m1,
                m2);

        assertTrue(encontrado.isPresent());
        assertEquals(m1, encontrado.get().getModule());
        assertEquals(m2, encontrado.get().getIncompatibleModule());
    }

    @Test
    @DisplayName("Deve validar existência de incompatibilidade entre dois módulos")
    void deveValidarExistenciaDeIncompatibilidade() {
        // nomes únicos para evitar violação de constraint
        Module m1 = moduleRepository.save(new Module("Auditoria_" + System.nanoTime(), "Controle e auditoria"));
        Module m2 = moduleRepository.save(new Module("Financeiro_" + System.nanoTime(), "Gestão financeira"));

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(m1, m2);
        incompatibilityRepository.save(incompatibility);

        boolean exists = incompatibilityRepository.existsByModuleAndIncompatibleModule(m1, m2);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve encontrar incompatibilidade inexistente")
    void naoDeveEncontrarIncompatibilidadeInexistente() {
        Module m1 = moduleRepository.save(new Module("RH", "Recursos Humanos"));
        Module m2 = moduleRepository.save(new Module("TI", "Tecnologia"));

        Optional<ModuleIncompatibility> encontrado = incompatibilityRepository.findByModuleAndIncompatibleModule(m1,
                m2);

        assertTrue(encontrado.isEmpty());
        assertFalse(incompatibilityRepository.existsByModuleAndIncompatibleModule(m1, m2));
    }
}
