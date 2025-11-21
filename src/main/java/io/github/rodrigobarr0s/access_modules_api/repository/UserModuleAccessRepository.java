package io.github.rodrigobarr0s.access_modules_api.repository;

import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserModuleAccessRepository extends JpaRepository<UserModuleAccess, Long> {
    List<UserModuleAccess> findByUser(User user);
    List<UserModuleAccess> findByModule(Module module);
}
