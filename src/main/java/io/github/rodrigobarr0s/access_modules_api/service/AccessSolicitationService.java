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
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
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
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", request.getUserId().toString()));

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", request.getModuleId().toString()));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa(request.getJustificativa());
        solicitation.setUrgente(request.isUrgente());
        solicitation.setCreatedAt(LocalDateTime.now());
        solicitation.setUpdatedAt(LocalDateTime.now());
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(180));
        solicitation.setProtocolo(generateProtocolo());

        // Validações de negócio
        String motivoNegacao = validarSolicitacao(user, module, request.getJustificativa());
        if (motivoNegacao == null) {
            solicitation.setStatus(SolicitationStatus.ATIVO);
        } else {
            solicitation.setStatus(SolicitationStatus.NEGADO);
            solicitation.setNegationReason(motivoNegacao);
        }

        return repository.save(solicitation);
    }

    private String validarSolicitacao(User user, Module module, String justificativa) {
        if (justificativa == null || justificativa.length() < 20 || justificativa.length() > 500) {
            return "Justificativa insuficiente ou genérica";
        }

        if (repository.existsByUserAndModuleAndStatus(user, module, SolicitationStatus.ATIVO.getCode())) {
            return "Usuário já possui solicitação ativa para este módulo";
        }

        if (user.getAccesses().stream().anyMatch(a -> a.getModule().equals(module))) {
            return "Usuário já possui acesso ativo a este módulo";
        }

        if (!departamentoPodeAcessar(user.getRole(), module)) {
            return "Departamento sem permissão para acessar este módulo";
        }

        if (moduloIncompativel(user, module)) {
            return "Módulo incompatível com outro módulo já ativo em seu perfil";
        }

        int limite = user.getRole() == Role.TI ? 10 : 5;
        if (user.getAccesses().size() >= limite) {
            return "Limite de módulos ativos atingido";
        }

        return null; // aprovado
    }

    private boolean departamentoPodeAcessar(Role role, Module module) {
        switch (role) {
            case TI:
                return true;
            case FINANCEIRO:
                return List.of("Gestão Financeira", "Relatórios Gerenciais", "Portal do Colaborador")
                        .contains(module.getName());
            case RH:
                return List.of("Administrador RH", "Colaborador RH", "Relatórios Gerenciais", "Portal do Colaborador")
                        .contains(module.getName());
            case OPERACOES:
                return List.of("Gestão de Estoque", "Compras", "Relatórios Gerenciais", "Portal do Colaborador")
                        .contains(module.getName());
            default:
                return List.of("Relatórios Gerenciais", "Portal do Colaborador").contains(module.getName());
        }
    }

    private boolean moduloIncompativel(User user, Module novoModulo) {
        return user.getAccesses().stream().anyMatch(a -> {
            String nome = a.getModule().getName();
            return (nome.equals("Aprovador Financeiro") && novoModulo.getName().equals("Solicitante Financeiro"))
                    || (nome.equals("Solicitante Financeiro") && novoModulo.getName().equals("Aprovador Financeiro"))
                    || (nome.equals("Administrador RH") && novoModulo.getName().equals("Colaborador RH"))
                    || (nome.equals("Colaborador RH") && novoModulo.getName().equals("Administrador RH"));
        });
    }

    @Transactional(readOnly = true)
    public List<AccessSolicitation> findWithFilters(SolicitationStatus status, Long userId, Long moduleId,
            Boolean urgente) {
        Specification<AccessSolicitation> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (status != null) {
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
        solicitation.setStatus(SolicitationStatus.CANCELADO);
        solicitation.setCancelReason(reason);
        solicitation.setUpdatedAt(LocalDateTime.now());
        return repository.save(solicitation);
    }

    @Transactional
    public AccessSolicitation renew(String protocolo) {
        AccessSolicitation solicitation = findByProtocolo(protocolo);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(180));
        solicitation.setUpdatedAt(LocalDateTime.now());
        solicitation.setProtocolo(generateProtocolo());

        String motivoNegacao = validarSolicitacao(solicitation.getUser(), solicitation.getModule(),
                solicitation.getJustificativa());
        if (motivoNegacao == null) {
            solicitation.setStatus(SolicitationStatus.ATIVO);
        } else {
            solicitation.setStatus(SolicitationStatus.NEGADO);
            solicitation.setNegationReason(motivoNegacao);
        }

        return repository.save(solicitation);
    }
}
