package io.github.rodrigobarr0s.access_modules_api.integration;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;
import io.github.rodrigobarr0s.access_modules_api.service.UserModuleAccessService;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserModuleAccessServiceIntegrationTest {

    @Autowired
    private UserModuleAccessService accessService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    @Test
    @DisplayName("Deve conceder acesso e recuperar por usuário")
    void grantAccessAndFindByUser() {
        User user = userService.save(new User(null, "user1", "123456", Role.ADMIN));
        Module module = moduleService.save(new Module(null, "mod1", "desc1"));

        UserModuleAccess access = accessService.grantAccess(user, module);

        assertNotNull(access.getId());
        assertEquals(user, access.getUser());
        assertEquals(module, access.getModule());
        assertNotNull(access.getGrantedAt());

        List<UserModuleAccess> accesses = accessService.findByUser(user);
        assertEquals(1, accesses.size());
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao conceder acesso duplicado")
    void grantAccess_shouldThrowDuplicateEntityException() {
        User user = userService.save(new User(null, "user2", "123456", Role.RH));
        Module module = moduleService.save(new Module(null, "mod2", "desc2"));

        accessService.grantAccess(user, module);

        assertThrows(DuplicateEntityException.class, () -> accessService.grantAccess(user, module));
    }

    @Test
    @DisplayName("Deve recuperar acessos por módulo")
    void findByModule_shouldReturnAccesses() {
        User user = userService.save(new User(null, "user3", "123456", Role.OPERACOES));
        Module module = moduleService.save(new Module(null, "mod3", "desc3"));

        accessService.grantAccess(user, module);

        List<UserModuleAccess> accesses = accessService.findByModule(module);
        assertEquals(1, accesses.size());
    }

    @Test
    @DisplayName("Deve revogar acesso existente")
    void revokeAccess_shouldDeleteAccess() {
        User user = userService.save(new User(null, "user4", "123456", Role.TI));
        Module module = moduleService.save(new Module(null, "mod4", "desc4"));

        UserModuleAccess access = accessService.grantAccess(user, module);
        Long accessId = access.getId();

        accessService.revokeAccess(accessId);

        List<UserModuleAccess> accesses = accessService.findByUser(user);
        assertTrue(accesses.isEmpty(), "Lista de acessos deve estar vazia após revogação");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar acessos de usuário inexistente")
    void findByUser_shouldThrowResourceNotFound() {
        User fakeUser = new User();
        fakeUser.setId(999L);
        fakeUser.setEmail("ghost");
        fakeUser.setPassword("123");
        fakeUser.setRole(Role.AUDITOR);

        assertThrows(ResourceNotFoundException.class, () -> accessService.findByUser(fakeUser));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar acessos de módulo inexistente")
    void findByModule_shouldThrowResourceNotFound() {
        Module fakeModule = new Module();
        fakeModule.setId(999L);
        fakeModule.setName("ghostModule");
        fakeModule.setDescription("desc");

        assertThrows(ResourceNotFoundException.class, () -> accessService.findByModule(fakeModule));
    }
}
