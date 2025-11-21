package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    @DisplayName("Deve salvar usuário quando não existe duplicidade")
    void shouldSaveUserSuccessfully() {
        User user = new User(null, "rodrigo", "123", Role.ADMIN);

        when(repository.findByUsername("rodrigo")).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(new User(1L, "rodrigo", "123", Role.ADMIN));

        User saved = service.save(user);

        assertNotNull(saved.getId());
        assertEquals("rodrigo", saved.getUsername());
        verify(repository).save(user);
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao salvar usuário já existente")
    void shouldThrowDuplicateEntityExceptionOnSave() {
        User user = new User(null, "rodrigo", "123", Role.ADMIN);

        when(repository.findByUsername("rodrigo")).thenReturn(Optional.of(user));

        assertThrows(DuplicateEntityException.class, () -> service.save(user));
    }

    @Test
    @DisplayName("Deve retornar usuário existente pelo username")
    void shouldFindUserByUsername() {
        User user = new User(1L, "rodrigo", "123", Role.ADMIN);

        when(repository.findByUsername("rodrigo")).thenReturn(Optional.of(user));

        User found = service.findByUsername("rodrigo");

        assertEquals("rodrigo", found.getUsername());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar usuário inexistente")
    void shouldThrowResourceNotFoundExceptionOnFindByUsername() {
        when(repository.findByUsername("rodrigo")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByUsername("rodrigo"));
    }

    @Test
    @DisplayName("Deve deletar usuário existente com sucesso")
    void shouldDeleteUserSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar usuário inexistente")
    void shouldThrowResourceNotFoundExceptionOnDelete() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    @DisplayName("Deve lançar DatabaseException ao ocorrer erro de integridade no delete")
    void shouldThrowDatabaseExceptionOnDeleteIntegrityViolation() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("erro"))
                .when(repository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> service.delete(1L));
    }
}
