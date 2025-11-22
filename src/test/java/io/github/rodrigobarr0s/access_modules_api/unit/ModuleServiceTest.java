package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository repository;

    @InjectMocks
    private ModuleService service;

    @Test
    @DisplayName("Deve retornar lista de módulos ao chamar findAll")
    void findAll_shouldReturnModules() {
        List<Module> modules = Arrays.asList(new Module(1L, "mod1", "desc1"));
        when(repository.findAll()).thenReturn(modules);

        List<Module> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("mod1", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar módulo existente ao buscar por nome")
    void findByName_shouldReturnModule() {
        Module module = new Module(1L, "mod1", "desc1");
        when(repository.findByName("mod1")).thenReturn(Optional.of(module));

        Module result = service.findByName("mod1");

        assertEquals("mod1", result.getName());
        verify(repository).findByName("mod1");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar módulo inexistente por nome")
    void findByName_shouldThrowResourceNotFound() {
        when(repository.findByName("modX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByName("modX"));
    }

    @Test
    @DisplayName("Deve salvar módulo novo com sucesso")
    void save_shouldPersistModule() {
        Module module = new Module(null, "mod1", "desc1");
        when(repository.findByName("mod1")).thenReturn(Optional.empty());
        when(repository.save(module)).thenReturn(new Module(1L, "mod1", "desc1"));

        Module result = service.save(module);

        assertNotNull(result.getId());
        verify(repository).save(module);
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao tentar salvar módulo duplicado")
    void save_shouldThrowDuplicateEntityException() {
        Module module = new Module(null, "mod1", "desc1");
        when(repository.findByName("mod1")).thenReturn(Optional.of(module));

        assertThrows(DuplicateEntityException.class, () -> service.save(module));
    }

    @Test
    @DisplayName("Deve remover módulo existente com sucesso")
    void delete_shouldRemoveModule() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar módulo inexistente")
    void delete_shouldThrowResourceNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    @DisplayName("Deve lançar DatabaseException ao ocorrer erro de integridade na deleção")
    void delete_shouldThrowDatabaseException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("error")).when(repository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> service.delete(1L));
    }

    @Test
    @DisplayName("Deve atualizar dados de módulo existente com sucesso")
    void update_shouldModifyModule() {
        Module existing = new Module(1L, "mod1", "desc1");
        Module update = new Module(null, "mod2", "desc2");

        when(repository.getReferenceById(1L)).thenReturn(existing);
        when(repository.save(existing)).thenReturn(existing);

        Module result = service.update(1L, update);

        assertEquals("mod2", result.getName());
        assertEquals("desc2", result.getDescription());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar módulo inexistente")
    void update_shouldThrowResourceNotFound() {
        when(repository.getReferenceById(99L)).thenThrow(new jakarta.persistence.EntityNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, new Module()));
    }
}

