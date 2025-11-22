package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    @DisplayName("Deve salvar usuário com senha criptografada quando não existe duplicidade")
    void shouldSaveUserSuccessfully() {
        User user = new User(null, "rodrigo", "123", Role.ADMIN);

        when(repository.findByEmail("rodrigo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(repository.save(any(User.class)))
                .thenReturn(new User(1L, "rodrigo", "encoded123", Role.ADMIN));

        User saved = service.save(user);

        assertNotNull(saved.getId());
        assertEquals("rodrigo", saved.getEmail());
        assertEquals("encoded123", saved.getPassword());
        verify(passwordEncoder).encode("123");
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao salvar usuário já existente")
    void shouldThrowDuplicateEntityExceptionOnSave() {
        User user = new User(null, "rodrigo", "123", Role.ADMIN);

        when(repository.findByEmail("rodrigo")).thenReturn(Optional.of(user));

        assertThrows(DuplicateEntityException.class, () -> service.save(user));
    }

    @Test
    @DisplayName("Deve retornar usuário existente pelo username")
    void shouldFindUserByUsername() {
        User user = new User(1L, "rodrigo", "encoded123", Role.ADMIN);

        when(repository.findByEmail("rodrigo")).thenReturn(Optional.of(user));

        User found = service.findByEmail("rodrigo");

        assertEquals("rodrigo", found.getEmail());
        assertEquals("encoded123", found.getPassword());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar usuário inexistente")
    void shouldThrowResourceNotFoundExceptionOnFindByUsername() {
        when(repository.findByEmail("rodrigo")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByEmail("rodrigo"));
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

    @Test
    @DisplayName("Deve atualizar usuário e criptografar nova senha")
    void shouldUpdateUserWithNewPassword() {
        User existing = new User(1L, "rodrigo", "encoded123", Role.ADMIN);
        User update = new User(null, "rodrigo", "novaSenha", Role.ADMIN);

        when(repository.getReferenceById(1L)).thenReturn(existing);
        when(passwordEncoder.encode("novaSenha")).thenReturn("encodedNovaSenha");
        when(repository.save(existing)).thenReturn(existing);

        User updated = service.update(1L, update);

        assertEquals("encodedNovaSenha", updated.getPassword());
        verify(passwordEncoder).encode("novaSenha");
        verify(repository).save(existing);
    }
}
