package io.github.rodrigobarr0s.access_modules_api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;

@Service
public class AccessSolicitationService {

    private final AccessSolicitationRepository repository;

    public AccessSolicitationService(AccessSolicitationRepository repository) {
        this.repository = repository;
    }

    // Geração de protocolo no formato SOL-YYYYMMDD-NNNN
    private String generateProtocolo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = repository.count() + 1; // simples, pode evoluir para sequence no banco
        return String.format("SOL-%s-%04d", datePart, sequence);
    }

    @Transactional
    public AccessSolicitation create(AccessSolicitation solicitation) {
        solicitation.setProtocolo(generateProtocolo());
        solicitation.setStatus(SolicitationStatus.PENDING);
        return repository.save(solicitation);
    }

    @Transactional(readOnly = true)
    public AccessSolicitation findByProtocolo(String protocolo) {
        return repository.findByProtocolo(protocolo)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
    }

    @Transactional
    public AccessSolicitation approve(String protocolo) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setStatus(SolicitationStatus.APPROVED);
        return repository.save(solicitation);
    }

    @Transactional
    public AccessSolicitation reject(String protocolo, String reason) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setStatus(SolicitationStatus.REJECTED);
        solicitation.setCancelReason(reason);
        return repository.save(solicitation);
    }

    @Transactional
    public AccessSolicitation cancel(String protocolo, String reason) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setStatus(SolicitationStatus.CANCELED);
        solicitation.setCancelReason(reason);
        return repository.save(solicitation);
    }

    @Transactional
    public AccessSolicitation renew(String protocolo) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setExpiresAt(LocalDateTime.now().plusMonths(6));
        solicitation.setProtocolo(generateProtocolo()); // novo protocolo
        solicitation.setStatus(SolicitationStatus.PENDING);
        return repository.save(solicitation);
    }

    @Transactional(readOnly = true)
    public List<AccessSolicitation> findByStatus(SolicitationStatus status) {
        return repository.findByStatus(status.getCode());
    }

    // Consulta com filtros dinâmicos
    @Transactional(readOnly = true)
    public List<AccessSolicitation> findWithFilters(SolicitationStatus status, Long userId, Long moduleId, Boolean urgente) {
        // Aqui pode evoluir para Specification/Criteria, mas deixo simplificado
        List<AccessSolicitation> all = repository.findAll();

        return all.stream()
                .filter(s -> status == null || s.getStatus() == status)
                .filter(s -> userId == null || (s.getUser() != null && s.getUser().getId().equals(userId)))
                .filter(s -> moduleId == null || (s.getModule() != null && s.getModule().getId().equals(moduleId)))
                .filter(s -> urgente == null || s.isUrgente() == urgente)
                .toList();
    }
}
