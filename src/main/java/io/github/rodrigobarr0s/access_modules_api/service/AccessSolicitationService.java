package io.github.rodrigobarr0s.access_modules_api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;

@Service
public class AccessSolicitationService {

    private final AccessSolicitationRepository repository;
    private final SolicitationSequenceRepository sequenceRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public AccessSolicitationService(AccessSolicitationRepository repository,
            SolicitationSequenceRepository sequenceRepository,
            UserRepository userRepository,
            ModuleRepository moduleRepository) {
        this.repository = repository;
        this.sequenceRepository = sequenceRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    // Geração de protocolo no formato SOL-YYYYMMDD-NNNN
    private String generateProtocolo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long sequence = sequenceRepository.getNextSequenceValue();
        return String.format("SOL-%s-%04d", datePart, sequence);
    }

    @Transactional
    public AccessSolicitation create(AccessSolicitationRequest request) {
        // Busca o usuário pelo ID
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", request.getUserId().toString()));

        // Busca o módulo pelo ID
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", request.getModuleId().toString()));

        // Cria a solicitação
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa(request.getJustificativa());
        solicitation.setUrgente(request.isUrgente());
        solicitation.setStatus(SolicitationStatus.PENDING);
        solicitation.setCreatedAt(LocalDateTime.now());
        solicitation.setUpdatedAt(LocalDateTime.now());
        solicitation.setExpiresAt(LocalDateTime.now().plusMonths(1));
        solicitation.setProtocolo(generateProtocolo());

        return repository.save(solicitation);
    }

    @Transactional(readOnly = true)
    public List<AccessSolicitation> findWithFilters(SolicitationStatus status, Long userId, Long moduleId,
            Boolean urgente) {
        Specification<AccessSolicitation> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (status != null) {
                // Converte o enum para o código numérico antes de comparar
                predicate = cb.and(predicate, cb.equal(root.get("status"), status.getCode()));
            }

            if (userId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
            }

            if (moduleId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("module").get("id"), moduleId));
            }

            if (urgente != null) {
                predicate = cb.and(predicate, cb.equal(root.get("urgente"), urgente));
            }

            return predicate;
        };

        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public AccessSolicitation findByProtocolo(String protocolo) {
        AccessSolicitation solicitation = repository.findByProtocolo(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", protocolo));

        // Segurança: só o dono pode acessar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = solicitation.getUser();
        if (user != null && !user.getEmail().equals(email)) {
            throw new AccessDeniedException("Usuário não autorizado a acessar esta solicitação");
        }

        return solicitation;
    }

    @Transactional
    public AccessSolicitation cancel(String protocolo, String reason) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setStatus(SolicitationStatus.CANCELED);
        solicitation.setCancelReason(reason);
        solicitation.setUpdatedAt(LocalDateTime.now());
        return repository.save(solicitation);
    }

    @Transactional
    public AccessSolicitation renew(String protocolo) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setExpiresAt(LocalDateTime.now().plusMonths(1));
        solicitation.setUpdatedAt(LocalDateTime.now());
        solicitation.setStatus(SolicitationStatus.PENDING);
        solicitation.setProtocolo(generateProtocolo()); // novo protocolo
        return repository.save(solicitation);
    }
}
