package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
class AccessSolicitationServiceIntegrationTest {

    @Autowired
    private AccessSolicitationService solicitationService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Deve salvar solicitação com status PENDING e createdAt preenchido")
    void save_shouldPersistWithCreatedAt() {
        User user = userService.save(new User(null, "user1", "123456", Role.ADMIN));
        Module module = moduleService.save(new Module(null, "mod1", "desc1"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);

        AccessSolicitation saved = solicitationService.save(solicitation);

        assertNotNull(saved.getId());
        assertEquals("PENDING", saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(2)));
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao salvar solicitação duplicada")
    void save_shouldThrowDuplicateEntityException() {
        User user = userService.save(new User(null, "user2", "123456", Role.RH));
        Module module = moduleService.save(new Module(null, "mod2", "desc2"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitationService.save(solicitation);

        AccessSolicitation duplicate = new AccessSolicitation();
        duplicate.setUser(user);
        duplicate.setModule(module);

        assertThrows(DuplicateEntityException.class, () -> solicitationService.save(duplicate));
    }

    @Test
    @DisplayName("Deve aprovar solicitação existente")
    void approve_shouldUpdateStatus() {
        User user = userService.save(new User(null, "user3", "123456", Role.OPERACOES));
        Module module = moduleService.save(new Module(null, "mod3", "desc3"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation = solicitationService.save(solicitation);

        AccessSolicitation approved = solicitationService.approve(solicitation.getId());

        assertEquals("APPROVED", approved.getStatus());
    }

    @Test
    @DisplayName("Deve rejeitar solicitação existente")
    void reject_shouldUpdateStatus() {
        User user = userService.save(new User(null, "user4", "123456", Role.TI));
        Module module = moduleService.save(new Module(null, "mod4", "desc4"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation = solicitationService.save(solicitation);

        AccessSolicitation rejected = solicitationService.reject(solicitation.getId());

        assertEquals("REJECTED", rejected.getStatus());
    }

    @Test
    @DisplayName("Deve remover solicitação existente")
    void delete_shouldRemoveSolicitation() {
        User user = userService.save(new User(null, "user5", "123456", Role.AUDITOR));
        Module module = moduleService.save(new Module(null, "mod5", "desc5"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation = solicitationService.save(solicitation);

        Long solicitationId = solicitation.getId();

        solicitationService.delete(solicitationId);

        assertThrows(ResourceNotFoundException.class, () -> solicitationService.findById(solicitationId));
    }
}
