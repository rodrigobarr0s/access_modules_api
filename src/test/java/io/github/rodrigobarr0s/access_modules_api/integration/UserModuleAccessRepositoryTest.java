package io.github.rodrigobarr0s.access_modules_api.integration;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserModuleAccessRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserModuleAccessRepositoryTest {

    @Autowired
    private UserModuleAccessRepository accessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private User createUser(String email) {
        return userRepository.save(new User(null, email, "123456", Role.ADMIN));
    }

    private Module createModule(String name) {
        return moduleRepository.save(new Module(null, name, "desc-" + name));
    }

    @Test
    @DisplayName("Deve salvar e buscar acessos por usuário")
    void deveSalvarEBuscarPorUsuario() {
        User user = createUser("user1@test.com");
        Module module = createModule("Financeiro_" + System.nanoTime());

        UserModuleAccess access = new UserModuleAccess(user, module);
        accessRepository.save(access);

        List<UserModuleAccess> encontrados = accessRepository.findByUser(user);

        assertFalse(encontrados.isEmpty());
        assertEquals(user, encontrados.get(0).getUser());
    }

    @Test
    @DisplayName("Deve salvar e buscar acessos por módulo")
    void deveSalvarEBuscarPorModulo() {
        User user = createUser("user2@test.com");
        Module module = createModule("RH_" + System.nanoTime());

        UserModuleAccess access = new UserModuleAccess(user, module);
        accessRepository.save(access);

        List<UserModuleAccess> encontrados = accessRepository.findByModule(module);

        assertFalse(encontrados.isEmpty());
        assertEquals(module, encontrados.get(0).getModule());
    }

    @Test
    @DisplayName("Deve buscar acesso específico por usuário e módulo")
    void deveBuscarPorUsuarioEModulo() {
        User user = createUser("user3@test.com");
        Module module = createModule("TI_" + System.nanoTime());

        UserModuleAccess access = new UserModuleAccess(user, module);
        accessRepository.save(access);

        Optional<UserModuleAccess> encontrado = accessRepository.findByUserAndModule(user, module);

        assertTrue(encontrado.isPresent());
        assertEquals(user, encontrado.get().getUser());
        assertEquals(module, encontrado.get().getModule());
    }

    @Test
    @DisplayName("Deve validar existência de acesso por usuário e módulo")
    void deveValidarExistenciaDeAcesso() {
        User user = createUser("user4@test.com");
        Module module = createModule("Operacoes_" + System.nanoTime());

        UserModuleAccess access = new UserModuleAccess(user, module);
        accessRepository.save(access);

        boolean exists = accessRepository.existsByUserAndModule(user, module);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve encontrar acesso inexistente")
    void naoDeveEncontrarAcessoInexistente() {
        User user = createUser("user5@test.com");
        Module module = createModule("Auditoria_" + System.nanoTime());

        Optional<UserModuleAccess> encontrado = accessRepository.findByUserAndModule(user, module);

        assertTrue(encontrado.isEmpty());
        assertFalse(accessRepository.existsByUserAndModule(user, module));
    }
}
