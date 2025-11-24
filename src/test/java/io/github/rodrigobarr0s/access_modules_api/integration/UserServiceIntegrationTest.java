package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService service;

    @Test
    @DisplayName("Deve salvar e recuperar usuário com sucesso no banco H2")
    void shouldSaveAndRetrieveUser() {
        User user = new User(null, "rodrigo@test.com", "123456", Role.ADMIN);
        User saved = service.save(user);

        assertNotNull(saved.getId());

        User found = service.findByEmail("rodrigo@test.com");
        assertEquals("rodrigo@test.com", found.getEmail());
        assertEquals(Role.ADMIN, found.getRole());

        // senha deve estar criptografada
        assertNotNull(found.getPassword());
        assertNotEquals("123456", found.getPassword());
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao salvar usuário já existente no banco")
    void shouldThrowDuplicateEntityException() {
        User user = new User(null, "maria@test.com", "abcdef", Role.RH);
        service.save(user);

        assertThrows(DuplicateEntityException.class, () -> service.save(user));
    }

    @Test
    @DisplayName("Deve listar todos os usuários salvos")
    void shouldFindAllUsers() {
        service.save(new User(null, "joao@test.com", "123456", Role.OPERACOES));
        service.save(new User(null, "ana@test.com", "abcdef", Role.ADMIN));

        List<User> users = service.findAll();

        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("joao@test.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("ana@test.com")));
    }

    @Test
    @DisplayName("Deve atualizar dados de usuário existente e criptografar nova senha")
    void shouldUpdateUserSuccessfully() {
        User user = service.save(new User(null, "carlos@test.com", "123456", Role.AUDITOR));

        User updated = new User(null, "carlos_updated@test.com", "abcdef", Role.ADMIN);
        User result = service.update(user.getId(), updated);

        assertEquals("carlos_updated@test.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());

        // senha deve estar criptografada
        assertNotNull(result.getPassword());
        assertNotEquals("abcdef", result.getPassword());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar usuário inexistente")
    void shouldThrowResourceNotFoundExceptionOnUpdate() {
        User updated = new User(null, "naoexiste@test.com", "123456", Role.TI);

        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, updated));
    }

    @Test
    @DisplayName("Deve deletar usuário existente com sucesso")
    void shouldDeleteUserSuccessfully() {
        User user = service.save(new User(null, "delete_me@test.com", "123456", Role.OPERACOES));

        service.delete(user.getId());

        assertThrows(ResourceNotFoundException.class, () -> service.findByEmail("delete_me@test.com"));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar usuário inexistente")
    void shouldThrowResourceNotFoundExceptionOnDelete() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
    }
}
