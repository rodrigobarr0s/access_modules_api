package io.github.rodrigobarr0s.access_modules_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;

public interface ModuleIncompatibilityRepository extends JpaRepository<ModuleIncompatibility, Long> {

    List<ModuleIncompatibility> findByModule(Module module);

    Optional<ModuleIncompatibility> findByModuleAndIncompatibleModule(Module module, Module incompatibleModule);

    boolean existsByModuleAndIncompatibleModule(Module module, Module incompatibleModule);
}
