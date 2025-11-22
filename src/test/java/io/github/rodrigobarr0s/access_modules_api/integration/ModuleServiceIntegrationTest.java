package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@SpringBootTest
@ActiveProfiles("test") 
@Transactional
class ModuleServiceIntegrationTest {

    @Autowired
    private ModuleService service;

    @Test
    @DisplayName("Deve salvar e recuperar módulo com sucesso")
    void saveAndFind_shouldPersistAndRetrieveModule() {
        Module module = new Module(null, "mod1", "desc1");
        Module saved = service.save(module);

        Module found = service.findByName("mod1");

        assertNotNull(saved.getId());
        assertEquals("mod1", found.getName());
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao salvar módulo duplicado")
    void save_shouldThrowDuplicateEntityException() {
        Module module = new Module(null, "mod2", "desc2");
        service.save(module);

        assertThrows(DuplicateEntityException.class, () -> service.save(new Module(null, "mod2", "descX")));
    }

    @Test
    @DisplayName("Deve listar múltiplos módulos")
    void findAll_shouldReturnMultipleModules() {
        service.save(new Module(null, "mod3", "desc3"));
        service.save(new Module(null, "mod4", "desc4"));

        List<Module> modules = service.findAll();

        assertTrue(modules.size() >= 2);
    }

    @Test
    @DisplayName("Deve atualizar dados de módulo existente")
    void update_shouldModifyModule() {
        Module module = service.save(new Module(null, "mod5", "desc5"));

        Module updated = service.update(module.getId(), new Module(null, "mod5-updated", "desc5-updated"));

        assertEquals("mod5-updated", updated.getName());
        assertEquals("desc5-updated", updated.getDescription());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar módulo inexistente")
    void update_shouldThrowResourceNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(999L, new Module(null, "modX", "descX")));
    }

    @Test
    @DisplayName("Deve deletar módulo existente com sucesso")
    void delete_shouldRemoveModule() {
        Module module = service.save(new Module(null, "mod6", "desc6"));

        service.delete(module.getId());

        assertThrows(ResourceNotFoundException.class, () -> service.findByName("mod6"));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar módulo inexistente")
    void delete_shouldThrowResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
    }
}
