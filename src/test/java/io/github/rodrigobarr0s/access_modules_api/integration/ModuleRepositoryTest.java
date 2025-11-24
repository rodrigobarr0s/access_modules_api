package io.github.rodrigobarr0s.access_modules_api.integration;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ModuleRepositoryTest {

    @Autowired
    private ModuleRepository moduleRepository;

    @Test
    @DisplayName("Deve salvar e buscar módulo por nome")
    void deveSalvarEBuscarPorNome() {
        Module module = new Module("Financeiro_" + System.nanoTime(), "Gestão financeira");
        moduleRepository.save(module);

        Optional<Module> encontrado = moduleRepository.findByName(module.getName());

        assertTrue(encontrado.isPresent());
        assertEquals("Financeiro_" + module.getName().split("_")[1], encontrado.get().getName());
        assertEquals("Gestão financeira", encontrado.get().getDescription());
    }

    @Test
    @DisplayName("Não deve encontrar módulo inexistente")
    void naoDeveEncontrarModuloInexistente() {
        Optional<Module> encontrado = moduleRepository.findByName("Inexistente_" + System.nanoTime());
        assertTrue(encontrado.isEmpty());
    }
}
