package io.github.rodrigobarr0s.access_modules_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.rodrigobarr0s.access_modules_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
