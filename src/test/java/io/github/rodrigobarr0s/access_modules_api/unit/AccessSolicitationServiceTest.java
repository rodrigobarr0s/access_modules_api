package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
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

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user.getEmail(), null));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(moduleRepository.findById(module.getId())).thenReturn(Optional.of(module));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);

        // retorna o próprio objeto salvo sem usar any()
        Answer<Object> returnsFirstArg = inv -> inv.getArgument(0);
        lenient().when(repository.save(argThat(obj -> obj instanceof AccessSolicitation))).thenAnswer(returnsFirstArg);
    }

    @Test
    @DisplayName("Deve aprovar solicitação com justificativa válida")
    void shouldApproveValidSolicitation() {
        AccessSolicitationRequest req = new AccessSolicitationRequest(
                List.of(module.getId()), "Justificativa detalhada e válida para acesso ao módulo", false);

        List<AccessSolicitation> solicitations = service.create(req);

        assertAll(
                () -> assertEquals(1, solicitations.size()),
                () -> assertEquals(SolicitationStatus.ATIVO, solicitations.get(0).getStatus()),
                () -> assertNull(solicitations.get(0).getNegationReason()));
    }

    @Test
    @DisplayName("Deve negar solicitação com justificativa nula")
    void shouldRejectNullJustification() {
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), null, false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Justificativa insuficiente ou genérica", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar solicitação com justificativa genérica")
    void shouldRejectGenericJustification() {
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "teste", false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Justificativa insuficiente ou genérica", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando usuário já possui acesso ao módulo")
    void shouldRejectUserAlreadyHasAccess() {
        user.addAccess(new UserModuleAccess(user, module));
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "Justificativa válida",
                false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Usuário já possui acesso ativo a este módulo", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando já existe solicitação ativa para o mesmo módulo")
    void shouldRejectActiveSolicitationExists() {
        when(repository.existsByUserAndModuleAndStatus(user, module, SolicitationStatus.ATIVO.getCode()))
                .thenReturn(true);
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "Justificativa válida",
                false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Usuário já possui solicitação ativa para este módulo", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando departamento não tem permissão")
    void shouldRejectDepartmentWithoutPermission() {
        user.setRole(Role.RH);
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "Justificativa válida",
                false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Departamento sem permissão para acessar este módulo", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando módulos são incompatíveis")
    void shouldRejectIncompatibleModules() {
        Module adminRH = new Module();
        adminRH.setId(10L);
        adminRH.setName("Administrador RH");
        user.addAccess(new UserModuleAccess(user, adminRH));
        Module colaboradorRH = new Module();
        colaboradorRH.setId(20L);
        colaboradorRH.setName("Colaborador RH");
        when(moduleRepository.findById(colaboradorRH.getId())).thenReturn(Optional.of(colaboradorRH));

        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(colaboradorRH.getId()),
                "Justificativa válida", false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Módulo incompatível com outro módulo já ativo em seu perfil",
                solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando limite de módulos ativos é atingido para não-TI")
    void shouldRejectLimitReachedNonTI() {
        user.setRole(Role.FINANCEIRO);
        for (int i = 0; i < 6; i++) {
            Module m = new Module();
            m.setId(100L + i);
            m.setName("Modulo " + i);
            user.addAccess(new UserModuleAccess(user, m));
        }
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "Justificativa válida",
                false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Limite de módulos ativos atingido", solicitations.get(0).getNegationReason());
    }

    @Test
    @DisplayName("Deve negar quando limite de módulos ativos é atingido para TI")
    void shouldRejectLimitReachedTI() {
        user.setRole(Role.TI);
        for (int i = 0; i < 11; i++) {
            Module m = new Module();
            m.setId(200L + i);
            m.setName("Modulo " + i);
            user.addAccess(new UserModuleAccess(user, m));
        }
        AccessSolicitationRequest req = new AccessSolicitationRequest(List.of(module.getId()), "Justificativa válida",
                false);
        List<AccessSolicitation> solicitations = service.create(req);
        assertEquals("Limite de módulos ativos atingido", solicitations.get(0).getNegationReason());
    }
}
