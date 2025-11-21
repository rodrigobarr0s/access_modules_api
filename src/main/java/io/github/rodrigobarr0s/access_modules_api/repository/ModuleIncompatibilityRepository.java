package io.github.rodrigobarr0s.access_modules_api.repository;

import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleIncompatibilityRepository extends JpaRepository<ModuleIncompatibility, Long> {
    List<ModuleIncompatibility> findByModule(Module module);
    List<ModuleIncompatibility> findByIncompatibleModule(Module incompatibleModule);
}
