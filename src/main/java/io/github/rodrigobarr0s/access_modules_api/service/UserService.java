package io.github.rodrigobarr0s.access_modules_api.service;

import org.springframework.stereotype.Service;

import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findByUsername(String userName) {
        return repository.findByUsername(userName).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
