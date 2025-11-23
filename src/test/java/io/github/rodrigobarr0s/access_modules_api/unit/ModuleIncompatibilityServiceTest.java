package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleIncompatibilityRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleIncompatibilityService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ModuleIncompatibilityServiceTest {

    @Mock
    private ModuleIncompatibilityRepository repository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleIncompatibilityService service;

    @Test
    @DisplayName("Deve retornar lista de incompatibilidades de um módulo existente")
    void findByModule_shouldReturnList() {
        Module module = new Module(1L, "mod1", "desc1");
        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(repository.findByModule(module)).thenReturn(Arrays.asList(new ModuleIncompatibility()));

        List<ModuleIncompatibility> result = service.findByModule(module);

        assertEquals(1, result.size());
        verify(repository).findByModule(module);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar incompatibilidades de módulo inexistente")
    void findByModule_shouldThrowResourceNotFound() {
        Module module = new Module(99L, "modX", "descX");
        when(moduleRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByModule(module));
    }

    @Test
    @DisplayName("Deve retornar true quando módulos forem incompatíveis")
    void isIncompatible_shouldReturnTrue() {
        Module module = new Module(1L, "mod1", "desc1");
        Module other = new Module(2L, "mod2", "desc2");
        ModuleIncompatibility incompatibility = new ModuleIncompatibility(module, other);

        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.existsById(2L)).thenReturn(true);
        when(repository.findByModule(module)).thenReturn(Arrays.asList(incompatibility));

        boolean result = service.isIncompatible(module, other);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false quando módulos não forem incompatíveis")
    void isIncompatible_shouldReturnFalse() {
        Module module = new Module(1L, "mod1", "desc1");
        Module other = new Module(2L, "mod2", "desc2");

        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.existsById(2L)).thenReturn(true);
        when(repository.findByModule(module)).thenReturn(Arrays.asList()); // sem incompatibilidades

        boolean result = service.isIncompatible(module, other);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve salvar incompatibilidade entre módulos")
    void addIncompatibility_shouldPersist() {
        Module module = new Module(1L, "mod1", "desc1");
        Module other = new Module(2L, "mod2", "desc2");

        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.existsById(2L)).thenReturn(true);
        when(repository.existsByModuleAndIncompatibleModule(module, other)).thenReturn(false);

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(module, other);
        when(repository.save(any(ModuleIncompatibility.class))).thenReturn(incompatibility);

        ModuleIncompatibility result = service.addIncompatibility(module, other);

        assertEquals(module, result.getModule());
        assertEquals(other, result.getIncompatibleModule());
        verify(repository).save(any(ModuleIncompatibility.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao tentar salvar incompatibilidade já existente")
    void addIncompatibility_shouldThrowDuplicateEntityException() {
        Module module = new Module(1L, "mod1", "desc1");
        Module other = new Module(2L, "mod2", "desc2");

        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.existsById(2L)).thenReturn(true);
        when(repository.existsByModuleAndIncompatibleModule(module, other)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.addIncompatibility(module, other));
    }

    @Test
    @DisplayName("Deve lançar DatabaseException ao tentar adicionar incompatibilidade consigo mesmo")
    void addIncompatibility_shouldThrowDatabaseExceptionForSelf() {
        Module module = new Module(1L, "mod1", "desc1");

        when(moduleRepository.existsById(1L)).thenReturn(true);

        assertThrows(DatabaseException.class, () -> service.addIncompatibility(module, module));
    }

    @Test
    @DisplayName("Deve remover incompatibilidade existente com sucesso")
    void removeIncompatibility_shouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        service.removeIncompatibility(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar remover incompatibilidade inexistente")
    void removeIncompatibility_shouldThrowResourceNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.removeIncompatibility(99L));
    }

    @Test
    @DisplayName("Deve lançar DatabaseException ao ocorrer erro de integridade na remoção")
    void removeIncompatibility_shouldThrowDatabaseException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("error")).when(repository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> service.removeIncompatibility(1L));
    }
}
