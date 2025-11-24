package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class AccessSolicitationServiceTest {

    @Mock
    private AccessSolicitationRepository solicitationRepository;
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
        user = new User(1L, "teste@empresa.com", "123456", Role.TI);
        module = new Module();
        module.setId(1L);
        module.setName("Gestão Financeira");

        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
    }

    @Test
    void deveCriarSolicitacaoComSucesso() {
        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(user.getId());
        request.setModuleId(module.getId());
        request.setJustificativa("Solicito acesso para realizar minhas atividades financeiras com urgência.");
        request.setUrgente(true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moduleRepository.findById(module.getId())).thenReturn(Optional.of(module));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation solicitation = service.create(request);

        assertNotNull(solicitation.getProtocolo());
        assertEquals(SolicitationStatus.ATIVO, solicitation.getStatus());
        assertNull(solicitation.getNegationReason());
    }

    @Test
    void deveNegarSolicitacaoPorJustificativaInsuficiente() {
        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(user.getId());
        request.setModuleId(module.getId());
        request.setJustificativa("teste"); // justificativa insuficiente
        request.setUrgente(false);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moduleRepository.findById(module.getId())).thenReturn(Optional.of(module));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation solicitation = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Justificativa insuficiente ou genérica", solicitation.getNegationReason());
    }

    @Test
    void deveNegarSolicitacaoPorDepartamentoSemPermissao() {
        User userFinanceiro = new User(2L, "financeiro@empresa.com", "123456", Role.FINANCEIRO);
        Module estoque = new Module();
        estoque.setId(2L);
        estoque.setName("Gestão de Estoque");

        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(userFinanceiro.getId());
        request.setModuleId(estoque.getId());
        request.setJustificativa("Preciso acessar estoque para controle de materiais.");
        request.setUrgente(false);

        when(userRepository.findById(userFinanceiro.getId())).thenReturn(Optional.of(userFinanceiro));
        when(moduleRepository.findById(estoque.getId())).thenReturn(Optional.of(estoque));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation solicitation = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Departamento sem permissão para acessar este módulo", solicitation.getNegationReason());
    }

    @Test
    void deveNegarSolicitacaoPorModuloIncompativel() {
        Module aprovador = new Module();
        aprovador.setId(3L);
        aprovador.setName("Aprovador Financeiro");
        user.addAccess(new UserModuleAccess(user, aprovador));

        Module solicitante = new Module();
        solicitante.setId(4L);
        solicitante.setName("Solicitante Financeiro");

        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(user.getId());
        request.setModuleId(solicitante.getId());
        request.setJustificativa("Preciso solicitar recursos financeiros.");
        request.setUrgente(false);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moduleRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation solicitation = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Módulo incompatível com outro módulo já ativo em seu perfil", solicitation.getNegationReason());
    }

    @Test
    void deveNegarSolicitacaoPorLimiteDeModulosAtivos() {
        User userOperacoes = new User(3L, "operacoes@empresa.com", "123456", Role.OPERACOES);

        // Simula 6 acessos ativos (limite é 5 para OPERACOES)
        for (int i = 1; i <= 6; i++) {
            Module m = new Module();
            m.setId((long) i);
            m.setName("Modulo " + i);

            UserModuleAccess acesso = new UserModuleAccess(userOperacoes, m);
            userOperacoes.addAccess(acesso);
        }

        Module novoModulo = new Module();
        novoModulo.setId(7L);
        novoModulo.setName("Compras");

        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(userOperacoes.getId());
        request.setModuleId(novoModulo.getId());
        request.setJustificativa("Preciso acessar compras para gestão de fornecedores.");
        request.setUrgente(false);

        when(userRepository.findById(userOperacoes.getId())).thenReturn(Optional.of(userOperacoes));
        when(moduleRepository.findById(novoModulo.getId())).thenReturn(Optional.of(novoModulo));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation solicitation = service.create(request);

        assertEquals(SolicitationStatus.NEGADO, solicitation.getStatus());
        assertEquals("Limite de módulos ativos atingido", solicitation.getNegationReason());
    }

    @Test
    void deveCancelarSolicitacao() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20251124-0001");
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setStatus(SolicitationStatus.ATIVO);

        when(solicitationRepository.findByProtocolo("SOL-20251124-0001"))
                .thenReturn(Optional.of(solicitation));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation cancelada = service.cancel("SOL-20251124-0001", "Não preciso mais");

        assertEquals(SolicitationStatus.CANCELADO, cancelada.getStatus());
        assertEquals("Não preciso mais", cancelada.getCancelReason());
    }

    @Test
    void deveRenovarSolicitacaoComSucesso() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-20251124-0001");
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa("Solicito acesso válido para continuar minhas atividades.");
        solicitation.setStatus(SolicitationStatus.ATIVO);

        when(solicitationRepository.findByProtocolo("SOL-20251124-0001"))
                .thenReturn(Optional.of(solicitation));
        when(solicitationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessSolicitation renovada = service.renew("SOL-20251124-0001");

        assertEquals(SolicitationStatus.ATIVO, renovada.getStatus());
        assertNotEquals("SOL-20251124-0001", renovada.getProtocolo()); // novo protocolo
        assertNotNull(renovada.getExpiresAt()); // validade estendida
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setUserId(99L);
        request.setModuleId(module.getId());
        request.setJustificativa("Solicitação válida");
        request.setUrgente(false);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }
}
