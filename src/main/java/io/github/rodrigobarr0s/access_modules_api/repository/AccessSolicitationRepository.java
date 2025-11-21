package io.github.rodrigobarr0s.access_modules_api.repository;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessSolicitationRepository extends JpaRepository<AccessSolicitation, Long> {
    List<AccessSolicitation> findByStatus(String status);
    List<AccessSolicitation> findByUser(User user);
    List<AccessSolicitation> findByModule(Module module);
}
