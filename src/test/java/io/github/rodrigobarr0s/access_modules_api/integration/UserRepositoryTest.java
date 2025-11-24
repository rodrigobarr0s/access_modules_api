package io.github.rodrigobarr0s.access_modules_api.integration;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve salvar e buscar usuário por email")
    void deveSalvarEBuscarPorEmail() {
        User user = new User("user@test.com", "123456", Role.ADMIN);
        userRepository.save(user);

        Optional<User> encontrado = userRepository.findByEmail("user@test.com");

        assertTrue(encontrado.isPresent());
        assertEquals("user@test.com", encontrado.get().getEmail());
        assertEquals(Role.ADMIN, encontrado.get().getRole());
    }

    @Test
    @DisplayName("Não deve encontrar usuário inexistente")
    void naoDeveEncontrarUsuarioInexistente() {
        Optional<User> encontrado = userRepository.findByEmail("naoexiste@test.com");
        assertTrue(encontrado.isEmpty());
    }
}
