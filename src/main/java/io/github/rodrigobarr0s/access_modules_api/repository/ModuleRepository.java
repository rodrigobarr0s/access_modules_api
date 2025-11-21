package io.github.rodrigobarr0s.access_modules_api.repository;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByName(String name);
}
