package io.github.rodrigobarr0s.access_modules_api.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AccessSolicitationService {

    private final AccessSolicitationRepository repository;

    public AccessSolicitationService(AccessSolicitationRepository repository) {
        this.repository = repository;
    }

    public AccessSolicitation save(AccessSolicitation solicitation) {
        repository.findByUserAndModuleAndStatus(
                solicitation.getUser(),
                solicitation.getModule(),
                "PENDING").ifPresent(s -> {
                    throw new DuplicateEntityException(
                            "Solicitação de acesso já existente para este usuário e módulo");
                });

        solicitation.setStatus("PENDING"); // força status inicial
        return repository.save(solicitation);
    }

    public List<AccessSolicitation> findAll() {
        return repository.findAll();
    }

    public AccessSolicitation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de acesso", "id=" + id));
    }

    public List<AccessSolicitation> findPending() {
        return repository.findByStatus("PENDING");
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitação de acesso", "id=" + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade ao deletar solicitação id=" + id, e);
        }
    }

    public AccessSolicitation approve(Long id) {
        try {
            AccessSolicitation solicitation = repository.getReferenceById(id);
            solicitation.setStatus("APPROVED");
            return repository.save(solicitation);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Solicitação de acesso", "id=" + id);
        }
    }

    public AccessSolicitation reject(Long id) {
        try {
            AccessSolicitation solicitation = repository.getReferenceById(id);
            solicitation.setStatus("REJECTED");
            return repository.save(solicitation);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Solicitação de acesso", "id=" + id);
        }
    }
}
