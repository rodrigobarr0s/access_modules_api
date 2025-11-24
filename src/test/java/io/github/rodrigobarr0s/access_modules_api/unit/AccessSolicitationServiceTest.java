package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setRole(Role.TI);

        module = new Module();
        module.setId(10L);
        module.setName("Gestão Financeira");

        when(userRepository.findByEmail(eq("user@test.com"))).thenReturn(Optional.of(user));
        when(moduleRepository.findById(eq(10L))).thenReturn(Optional.of(module));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
    }

    @Test
    @DisplayName("Deve criar solicitação aprovada quando justificativa é válida")
    void testCreateApproved() {
        AccessSolicitationRequest request = new AccessSolicitationRequest(
                10L,
                "Justificativa válida com mais de 20 caracteres",
                true);

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        assertEquals(SolicitationStatus.ATIVO, result.getStatus());
        assertEquals(user, result.getUser());
        assertEquals(module, result.getModule());

        // Verifica que o save foi chamado com o objeto correto
        verify(repository).save(captor.getValue());
        verify(userRepository).findByEmail(eq("user@test.com"));
        verify(moduleRepository).findById(eq(10L));
    }

    @Test
    @DisplayName("Deve negar solicitação quando justificativa é curta")
    void testCreateDeniedShortJustification() {
        AccessSolicitationRequest request = new AccessSolicitationRequest(10L, "Curta", false);

        // Captura o objeto passado ao save
        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        // Verificações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Justificativa insuficiente ou genérica", result.getNegationReason());

        // Verifica que o save foi chamado com o objeto correto
        verify(repository).save(captor.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado")
    void testCreateUserNotFound() {
        when(userRepository.findByEmail(eq("user@test.com"))).thenReturn(Optional.empty());
        AccessSolicitationRequest request = new AccessSolicitationRequest(10L, "Justificativa válida", false);

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando módulo não é encontrado")
    void testCreateModuleNotFound() {
        when(moduleRepository.findById(eq(10L))).thenReturn(Optional.empty());
        AccessSolicitationRequest request = new AccessSolicitationRequest(10L, "Justificativa válida", false);

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    @DisplayName("Deve retornar solicitação quando protocolo pertence ao usuário autenticado")
    void testFindByProtocoloAuthorized() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20250101-0001");
        solicitation.setUser(user);

        when(repository.findByProtocolo(eq("SOL-20250101-0001"))).thenReturn(Optional.of(solicitation));

        AccessSolicitation result = service.findByProtocolo("SOL-20250101-0001");
        assertEquals(solicitation, result);
        verify(repository).findByProtocolo(eq("SOL-20250101-0001"));
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso negado quando protocolo pertence a outro usuário")
    void testFindByProtocoloUnauthorized() {
        User otherUser = new User();
        otherUser.setEmail("other@test.com");

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20250101-0002");
        solicitation.setUser(otherUser);

        when(repository.findByProtocolo(eq("SOL-20250101-0002"))).thenReturn(Optional.of(solicitation));

        assertThrows(AccessDeniedException.class, () -> service.findByProtocolo("SOL-20250101-0002"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando protocolo não é encontrado")
    void testFindByProtocoloNotFound() {
        when(repository.findByProtocolo(eq("SOL-20250101-0003"))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findByProtocolo("SOL-20250101-0003"));
    }

    @Test
    @DisplayName("Deve cancelar solicitação existente e atualizar status para CANCELADO")
    void testCancelSolicitation() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20250101-0004");
        solicitation.setUser(user);

        when(repository.findByProtocolo(eq("SOL-20250101-0004"))).thenReturn(Optional.of(solicitation));
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.cancel("SOL-20250101-0004", "Motivo cancelamento");
        assertEquals(SolicitationStatus.CANCELADO, result.getStatus());
        verify(repository).save(eq(solicitation));
    }

    @Test
    @DisplayName("Deve renovar solicitação válida e manter status ATIVO")
    void testRenewApproved() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20250101-0005");
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa("Justificativa válida para renovação");

        when(repository.findByProtocolo(eq("SOL-20250101-0005"))).thenReturn(Optional.of(solicitation));
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.renew("SOL-20250101-0005");
        assertEquals(SolicitationStatus.ATIVO, result.getStatus());
        verify(repository).save(eq(solicitation));
    }

    @Test
    @DisplayName("Deve negar renovação quando justificativa é insuficiente")
    void testRenewDenied() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20250101-0006");
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa("Curta");

        when(repository.findByProtocolo(eq("SOL-20250101-0006"))).thenReturn(Optional.of(solicitation));
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.renew("SOL-20250101-0006");
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        verify(repository).save(eq(solicitation));
    }

    @Test
    @DisplayName("Deve aplicar filtros e retornar lista com resultados")
    void testFindWithFilters() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setUrgente(true);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<AccessSolicitation>> captor = ArgumentCaptor.forClass(Specification.class);

        when(repository.findAll(captor.capture())).thenReturn(List.of(solicitation));

        // Chama o método com filtros preenchidos
        List<AccessSolicitation> result = service.findWithFilters(
                SolicitationStatus.ATIVO,
                user.getId(),
                module.getId(),
                true);

        // Verificações
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(solicitation, result.get(0));

        // Garante que o repository foi chamado com a Specification capturada
        verify(repository).findAll(captor.getValue());
    }

    @Test
    @DisplayName("Deve negar solicitação quando usuário já possui acesso ativo ao módulo")
    void testCreateDeniedUserAlreadyHasAccess() {
        UserModuleAccess access = new UserModuleAccess(user, module);
        user.addAccess(access); // associa o módulo ao usuário

        AccessSolicitationRequest request = new AccessSolicitationRequest(10L, "Justificativa válida", false);

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Usuário já possui acesso ativo a este módulo", result.getNegationReason());
        verify(repository).save(captor.getValue());
    }

    @Test
    @DisplayName("Deve negar solicitação quando departamento não tem permissão para acessar módulo")
    void testCreateDeniedDepartmentWithoutPermission() {
        user.setRole(Role.OPERACOES); // este role não tem acesso ao módulo Gestão Financeira

        AccessSolicitationRequest request = new AccessSolicitationRequest(10L, "Justificativa válida", false);

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Departamento sem permissão para acessar este módulo", result.getNegationReason());
        verify(repository).save(captor.getValue());
    }

    @Test
    @DisplayName("Deve negar solicitação quando módulo é incompatível com outro já ativo")
    void testCreateDeniedIncompatibleModules() {
        // Módulo já ativo no usuário
        Module aprovadorFinanceiro = new Module();
        aprovadorFinanceiro.setId(11L);
        aprovadorFinanceiro.setName("Aprovador Financeiro");

        UserModuleAccess access = new UserModuleAccess(user, aprovadorFinanceiro);
        user.addAccess(access);

        // Novo módulo solicitado (incompatível)
        Module solicitanteFinanceiro = new Module();
        solicitanteFinanceiro.setId(12L);
        solicitanteFinanceiro.setName("Solicitante Financeiro");

        when(moduleRepository.findById(eq(12L))).thenReturn(Optional.of(solicitanteFinanceiro));

        AccessSolicitationRequest request = new AccessSolicitationRequest(
                12L,
                "Justificativa válida",
                false);

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Módulo incompatível com outro módulo já ativo em seu perfil",
                result.getNegationReason());
        verify(repository).save(captor.getValue());
    }

    @Disabled
    @Test
    @DisplayName("Deve negar solicitação quando limite de módulos ativos é atingido")
    void testCreateDeniedLimitReached() {
        // Adiciona 5 acessos ao mesmo user configurado no setUp()
        for (int i = 0; i < 5; i++) {
            Module m = new Module();
            m.setId((long) i);
            m.setName("Modulo " + i);

            UserModuleAccess access = new UserModuleAccess(user, m);
            user.addAccess(access);
        }

        // Garante que o mock devolve esse mesmo user atualizado
        when(userRepository.findByEmail(eq("user@test.com"))).thenReturn(Optional.of(user));

        AccessSolicitationRequest request = new AccessSolicitationRequest(
                10L,
                "Justificativa válida com mais de 20 caracteres",
                false);

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation result = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Limite de módulos ativos atingido", result.getNegationReason());
        verify(repository).save(captor.getValue());
    }

}
