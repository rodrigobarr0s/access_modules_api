package io.github.rodrigobarr0s.access_modules_api.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleIncompatibilityRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

@Service
public class ModuleIncompatibilityService {

    private final ModuleIncompatibilityRepository repository;
    private final ModuleRepository moduleRepository;

    public ModuleIncompatibilityService(ModuleIncompatibilityRepository repository,
                                        ModuleRepository moduleRepository) {
        this.repository = repository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public List<ModuleIncompatibility> findByModule(Module module) {
        validateModuleExists(module);
        return repository.findByModule(module);
    }

    @Transactional(readOnly = true)
    public boolean isIncompatible(Module module, Module other) {
        validateModuleExists(module);
        validateModuleExists(other);
        return repository.findByModule(module).stream()
                .anyMatch(inc -> inc.getIncompatibleModule().equals(other));
    }

    @Transactional
    public ModuleIncompatibility addIncompatibility(Module module, Module other) {
        validateModuleExists(module);
        validateModuleExists(other);

        if (module.equals(other)) {
            throw new DatabaseException("Um módulo não pode ser incompatível consigo mesmo");
        }

        if (existsAlready(module, other)) {
            throw new DuplicateEntityException("Incompatibilidade já cadastrada",
                    "módulo=" + module.getName() + ", incompatível=" + other.getName());
        }

        ModuleIncompatibility incompatibility = new ModuleIncompatibility(module, other);
        return repository.save(incompatibility);
    }

    @Transactional
    public void removeIncompatibility(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Incompatibilidade de módulo", "id=" + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade ao remover incompatibilidade id=" + id, e);
        }
    }

    // Métodos auxiliares
    private void validateModuleExists(Module module) {
        if (module == null || module.getId() == null || !moduleRepository.existsById(module.getId())) {
            throw new ResourceNotFoundException("Módulo", "id=" + (module != null ? module.getId() : "null"));
        }
    }

    private boolean existsAlready(Module module, Module other) {
        return repository.existsByModuleAndIncompatibleModule(module, other);
    }
}

