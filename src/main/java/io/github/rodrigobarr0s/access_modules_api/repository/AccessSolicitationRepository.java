package io.github.rodrigobarr0s.access_modules_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;

public interface AccessSolicitationRepository extends JpaRepository<AccessSolicitation, Long> {

    Optional<AccessSolicitation> findByProtocolo(String protocolo);

    List<AccessSolicitation> findByUser(User user);

    List<AccessSolicitation> findByModule(Module module);

    List<AccessSolicitation> findByStatus(Integer status); // usa o código do enum
}
