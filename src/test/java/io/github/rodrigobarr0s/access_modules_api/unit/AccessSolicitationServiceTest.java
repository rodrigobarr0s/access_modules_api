package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

class AccessSolicitationServiceTest {

    @Mock
    private AccessSolicitationRepository repository;

    @Mock
    private SolicitationSequenceRepository sequenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private AccessSolicitationService service;

    private User user;
    private Module module;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setRole(Role.TI);

        module = new Module();
        module.setId(1L);
        module.setName("Gestão Financeira");

        // Autenticação fake no contexto
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user.getEmail(), null));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(moduleRepository.findById(module.getId())).thenReturn(Optional.of(module));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Deve criar solicitação aprovada quando justificativa válida")
    void shouldCreateApprovedSolicitation() {
        AccessSolicitationRequest req = new AccessSolicitationRequest();
        req.setModuleId(module.getId());
        req.setJustificativa("Justificativa detalhada e válida para acesso ao módulo");
        req.setUrgente(false);

        AccessSolicitation solicitation = service.create(req);

        assertEquals(SolicitationStatus.ATIVO, solicitation.getStatus());
        assertNull(solicitation.getNegationReason());
    }

    @Test
    @DisplayName("Deve negar solicitação com justificativa curta")
    void shouldRejectShortJustification() {
        AccessSolicitationRequest req = new AccessSolicitationRequest();
        req.setModuleId(module.getId());
        req.setJustificativa("curta"); // < 20 chars
        req.setUrgente(false);

        AccessSolicitation solicitation = service.create(req);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Justificativa insuficiente ou genérica", solicitation.getNegationReason());
    }

    @Test
    @DisplayName("Deve negar solicitação quando usuário já possui acesso ao módulo")
    void shouldRejectUserAlreadyHasAccess() {
        UserModuleAccess access = new UserModuleAccess();
        access.setModule(module);
        user.addAccess(access); // ✅ usa helper da entidade

        AccessSolicitationRequest req = new AccessSolicitationRequest();
        req.setModuleId(module.getId());
        req.setJustificativa("Justificativa detalhada e válida para acesso ao módulo");
        req.setUrgente(false);

        AccessSolicitation solicitation = service.create(req);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Usuário já possui acesso ativo a este módulo", solicitation.getNegationReason());
    }

    @Test
    @DisplayName("Deve negar solicitação quando departamento não tem permissão para acessar módulo")
    void shouldRejectDepartmentWithoutPermission() {
        // Usuário de RH tentando acessar módulo Financeiro
        user.setRole(Role.RH);

        AccessSolicitationRequest req = new AccessSolicitationRequest();
        req.setModuleId(module.getId()); // módulo "Gestão Financeira"
        req.setJustificativa("Justificativa detalhada e válida para acesso ao módulo");
        req.setUrgente(false);

        AccessSolicitation solicitation = service.create(req);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Departamento sem permissão para acessar este módulo", solicitation.getNegationReason());
    }

}
