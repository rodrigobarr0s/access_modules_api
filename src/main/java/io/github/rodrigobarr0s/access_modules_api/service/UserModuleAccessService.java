package io.github.rodrigobarr0s.access_modules_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserModuleAccessRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@Service
public class UserModuleAccessService {

    private final UserModuleAccessRepository repository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public UserModuleAccessService(UserModuleAccessRepository repository,
            UserRepository userRepository,
            ModuleRepository moduleRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<UserModuleAccess> findByUser(User user) {
        if (user == null || user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new ResourceNotFoundException("Usuário", "id=" + (user != null ? user.getId() : "null"));
        }
        return repository.findByUser(user);
    }

    public List<UserModuleAccess> findByModule(Module module) {
        if (module == null || module.getId() == null || !moduleRepository.existsById(module.getId())) {
            throw new ResourceNotFoundException("Módulo", "id=" + (module != null ? module.getId() : "null"));
        }
        return repository.findByModule(module);
    }

    public UserModuleAccess grantAccess(User user, Module module) {
        if (repository.existsByUserAndModule(user, module)) {
            throw new DuplicateEntityException(
                    "Acesso de usuário a módulo",
                    "usuário=" + user.getUsername() + ", módulo=" + module.getName());
        }

        UserModuleAccess access = new UserModuleAccess();
        access.setUser(user);
        access.setModule(module);
        access.setGrantedAt(LocalDateTime.now());
        return repository.save(access);
    }

    public void revokeAccess(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Acesso de usuário a módulo", "id=" + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade ao revogar acesso id=" + id, e);
        }
    }
}
