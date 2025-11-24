package io.github.rodrigobarr0s.access_modules_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;

public interface AccessSolicitationRepository
        extends JpaRepository<AccessSolicitation, Long>, JpaSpecificationExecutor<AccessSolicitation> {

    Optional<AccessSolicitation> findByProtocolo(String protocolo);

    // método para validar se já existe solicitação ativa
    boolean existsByUserAndModuleAndStatus(User user, Module module, Integer status);
}
