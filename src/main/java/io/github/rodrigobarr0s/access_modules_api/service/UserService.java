package io.github.rodrigobarr0s.access_modules_api.service;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByEmail(String userName) {
        return repository.findByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userName));
    }

    @Transactional
    public User save(User user) {
        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new DuplicateEntityException("Usuário", user.getEmail());
                });

        // Criptografa a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", "id=" + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade ao deletar usuário id=" + id, e);
        }
    }

    @Transactional
    public User update(Long id, User obj) {
        try {
            User entity = repository.getReferenceById(id);
            updateData(entity, obj);
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário", "id=" + id);
        }
    }

    private void updateData(User entity, User obj) {
        entity.setEmail(Objects.requireNonNullElse(obj.getEmail(), entity.getEmail()));

        if (obj.getPassword() != null) {
            // Criptografa a nova senha
            entity.setPassword(passwordEncoder.encode(obj.getPassword()));
        }

        entity.setRole(Objects.requireNonNullElse(obj.getRole(), entity.getRole()));
    }

}
