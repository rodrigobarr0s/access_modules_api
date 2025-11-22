package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserModuleAccessRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.UserModuleAccessService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserModuleAccessServiceTest {

    @Mock
    private UserModuleAccessRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private UserModuleAccessService service;

    @Test
    @DisplayName("Deve retornar acessos por usuário existente")
    void findByUser_shouldReturnAccesses() {
        User user = new User(1L, "user1", "123456", Role.ADMIN);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(repository.findByUser(user)).thenReturn(Arrays.asList(new UserModuleAccess()));

        List<UserModuleAccess> result = service.findByUser(user);

        assertEquals(1, result.size());
        verify(repository).findByUser(user);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar acessos por usuário inexistente")
    void findByUser_shouldThrowResourceNotFound() {
        User user = new User(99L, "userX", "123456", Role.RH);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByUser(user));
    }

    @Test
    @DisplayName("Deve retornar acessos por módulo existente")
    void findByModule_shouldReturnAccesses() {
        Module module = new Module(1L, "mod1", "desc1");
        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(repository.findByModule(module)).thenReturn(Arrays.asList(new UserModuleAccess()));

        List<UserModuleAccess> result = service.findByModule(module);

        assertEquals(1, result.size());
        verify(repository).findByModule(module);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar acessos por módulo inexistente")
    void findByModule_shouldThrowResourceNotFound() {
        Module module = new Module(99L, "modX", "descX");
        when(moduleRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByModule(module));
    }

    @Test
    @DisplayName("Deve conceder acesso a usuário e módulo novos")
    void grantAccess_shouldPersistAccess() {
        User user = new User(1L, "user1", "123456", Role.ADMIN);
        Module module = new Module(1L, "mod1", "desc1");

        when(repository.existsByUserAndModule(user, module)).thenReturn(false);
        when(repository.save(any(UserModuleAccess.class)))
                .thenAnswer(invocation -> {
                    UserModuleAccess access = invocation.getArgument(0);
                    access.setId(1L);
                    return access;
                });

        UserModuleAccess result = service.grantAccess(user, module);

        assertNotNull(result.getId());
        assertEquals(user, result.getUser());
        assertEquals(module, result.getModule());
        assertNotNull(result.getGrantedAt());
        verify(repository).save(any(UserModuleAccess.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateEntityException ao conceder acesso já existente")
    void grantAccess_shouldThrowDuplicateEntityException() {
        User user = new User(1L, "user1", "123456", Role.ADMIN);
        Module module = new Module(1L, "mod1", "desc1");

        when(repository.existsByUserAndModule(user, module)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.grantAccess(user, module));
    }

    @Test
    @DisplayName("Deve revogar acesso existente")
    void revokeAccess_shouldDeleteAccess() {
        when(repository.existsById(1L)).thenReturn(true);

        service.revokeAccess(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao revogar acesso inexistente")
    void revokeAccess_shouldThrowResourceNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.revokeAccess(99L));
    }

    @Test
    @DisplayName("Deve lançar DatabaseException ao ocorrer erro de integridade na revogação")
    void revokeAccess_shouldThrowDatabaseException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("error")).when(repository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> service.revokeAccess(1L));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário é null")
    void findByUser_shouldThrowWhenUserIsNull() {
        assertThrows(ResourceNotFoundException.class, () -> service.findByUser(null));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário tem id null")
    void findByUser_shouldThrowWhenUserIdIsNull() {
        User user = new User();
        user.setEmail("userX");
        assertThrows(ResourceNotFoundException.class, () -> service.findByUser(user));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando módulo é null")
    void findByModule_shouldThrowWhenModuleIsNull() {
        assertThrows(ResourceNotFoundException.class, () -> service.findByModule(null));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando módulo tem id null")
    void findByModule_shouldThrowWhenModuleIdIsNull() {
        Module module = new Module();
        module.setName("modX");
        assertThrows(ResourceNotFoundException.class, () -> service.findByModule(module));
    }

}
