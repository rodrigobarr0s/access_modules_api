package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleIncompatibilityService;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ModuleIncompatibilityServiceIntegrationTest {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleIncompatibilityService incompatibilityService;

    @Test
    @DisplayName("Deve salvar e recuperar incompatibilidade entre módulos")
    void addAndFindIncompatibility_shouldPersistAndRetrieve() {
        Module modA = moduleService.save(new Module(null, "modA", "descA"));
        Module modB = moduleService.save(new Module(null, "modB", "descB"));

        incompatibilityService.addIncompatibility(modA, modB);

        List<ModuleIncompatibility> result = incompatibilityService.findByModule(modA);

        assertEquals(1, result.size());
        assertEquals(modB.getId(), result.get(0).getIncompatibleModule().getId());
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao adicionar incompatibilidade já existente")
    void addIncompatibility_shouldThrowDuplicateEntityException() {
        Module modA = moduleService.save(new Module(null, "modC", "descC"));
        Module modB = moduleService.save(new Module(null, "modD", "descD"));

        incompatibilityService.addIncompatibility(modA, modB);

        assertThrows(DuplicateEntityException.class,
                () -> incompatibilityService.addIncompatibility(modA, modB));
    }

    @Test
    @DisplayName("Deve verificar se módulos são incompatíveis")
    void isIncompatible_shouldReturnTrue() {
        Module modA = moduleService.save(new Module(null, "modE", "descE"));
        Module modB = moduleService.save(new Module(null, "modF", "descF"));

        incompatibilityService.addIncompatibility(modA, modB);

        assertTrue(incompatibilityService.isIncompatible(modA, modB));
    }

    @Test
    @DisplayName("Deve remover incompatibilidade existente com sucesso")
    void removeIncompatibility_shouldDelete() {
        Module modA = moduleService.save(new Module(null, "modG", "descG"));
        Module modB = moduleService.save(new Module(null, "modH", "descH"));

        ModuleIncompatibility incompatibility = incompatibilityService.addIncompatibility(modA, modB);

        incompatibilityService.removeIncompatibility(incompatibility.getId());

        List<ModuleIncompatibility> result = incompatibilityService.findByModule(modA);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao remover incompatibilidade inexistente")
    void removeIncompatibility_shouldThrowResourceNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> incompatibilityService.removeIncompatibility(999L));
    }
}
