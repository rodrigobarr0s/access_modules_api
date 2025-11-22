package io.github.rodrigobarr0s.access_modules_api.service;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ModuleService {

    private final ModuleRepository repository;

    public ModuleService(ModuleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Module> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Module findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", name));
    }

    @Transactional
    public Module save(Module module) {
        repository.findByName(module.getName())
                .ifPresent(m -> {
                    throw new DuplicateEntityException("Módulo", module.getName());
                });
        return repository.save(module);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Módulo", "id=" + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade ao deletar módulo id=" + id, e);
        }
    }

    @Transactional
    public Module update(Long id, Module obj) {
        try {
            Module entity = repository.getReferenceById(id);
            updateData(entity, obj);
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Módulo", "id=" + id);
        }
    }

    private void updateData(Module entity, Module obj) {
        entity.setName(Objects.requireNonNullElse(obj.getName(), entity.getName()));
        entity.setDescription(Objects.requireNonNullElse(obj.getDescription(), entity.getDescription()));
    }
}
