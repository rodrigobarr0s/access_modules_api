package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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

class AccessSolicitationServiceExtraTest {

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
    private AccessSolicitation solicitation;

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

        solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa("Justificativa válida e detalhada");
        solicitation.setStatus(SolicitationStatus.ATIVO);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user.getEmail(), null));
    }

    @Test
    @DisplayName("findWithFilters deve aplicar todos os filtros")
    void shouldApplyAllFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AccessSolicitation> page = new PageImpl<>(List.of(solicitation), pageable, 1);

        when(repository.findAll(
                ArgumentMatchers.<Specification<AccessSolicitation>>argThat(spec -> spec != null),
                ArgumentMatchers.<Pageable>argThat(p -> p.getPageNumber() == 0 &&
                        p.getPageSize() == 10 &&
                        p.getSort().getOrderFor("createdAt") != null)))
                .thenReturn(page);

        Page<AccessSolicitation> result = service.findWithFilters(
                SolicitationStatus.ATIVO,
                module.getId(),
                true,
                "financeira",
                LocalDate.now().minusDays(1),
                LocalDate.now(),
                pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(repository).findAll(
                ArgumentMatchers.<Specification<AccessSolicitation>>argThat(spec -> spec != null),
                ArgumentMatchers.<Pageable>argThat(p -> p.getPageNumber() == 0 &&
                        p.getPageSize() == 10 &&
                        p.getSort().getOrderFor("createdAt") != null));
    }

    @Test
    @DisplayName("findWithFilters sem filtros deve retornar todos")
    void shouldReturnAllWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AccessSolicitation> page = new PageImpl<>(List.of(solicitation), pageable, 1);

        when(repository.findAll(
                ArgumentMatchers.<Specification<AccessSolicitation>>argThat(spec -> spec != null),
                ArgumentMatchers.<Pageable>argThat(p -> p.getPageSize() == 10)))
                .thenReturn(page);

        Page<AccessSolicitation> result = service.findWithFilters(
                null, null, null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(repository).findAll(
                ArgumentMatchers.<Specification<AccessSolicitation>>argThat(spec -> spec != null),
                ArgumentMatchers.<Pageable>argThat(p -> p.getPageSize() == 10));
    }

    @Test
    @DisplayName("findByProtocolo deve retornar solicitação autorizada")
    void shouldFindByProtocoloAuthorized() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setProtocolo("SOL-123");

        // mock ajustado para usar findByProtocoloWithHistory
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));

        // execução
        AccessSolicitation result = service.findByProtocolo("SOL-123");

        // validações
        assertEquals(solicitation, result);

        // verifica chamada correta
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
    }

    @Test
    @DisplayName("findByProtocolo deve lançar AccessDeniedException para usuário diferente")
    void shouldThrowAccessDeniedForDifferentUser() {
        // cria outro usuário diferente do autenticado
        User otherUser = new User();
        otherUser.setEmail("other@email.com");
        solicitation.setUser(otherUser);
        solicitation.setProtocolo("SOL-123");

        // mock ajustado para usar findByProtocoloWithHistory
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));

        // autenticação com usuário diferente
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@email.com", null));

        // execução e validação
        assertThrows(AccessDeniedException.class, () -> service.findByProtocolo("SOL-123"));

        // verifica chamada correta
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
    }

    @Test
    @DisplayName("findByProtocolo deve lançar ResourceNotFoundException quando não encontrado")
    void shouldThrowNotFoundWhenProtocoloMissing() {
        // mock ajustado para usar findByProtocoloWithHistory
        when(repository.findByProtocoloWithHistory(eq("SOL-404"))).thenReturn(Optional.empty());

        // execução e validação
        assertThrows(ResourceNotFoundException.class, () -> service.findByProtocolo("SOL-404"));

        // verifica chamada correta
        verify(repository).findByProtocoloWithHistory(eq("SOL-404"));
    }

    @Test
    @DisplayName("cancel deve atualizar status para CANCELADO e salvar")
    void shouldCancelSolicitation() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        // execução
        AccessSolicitation result = service.cancel("SOL-123", "Motivo de cancelamento válido");

        // validações
        assertEquals(SolicitationStatus.CANCELADO, result.getStatus());
        assertEquals("Motivo de cancelamento válido", result.getCancelReason());
        assertEquals(solicitation, result); // continua sendo o mesmo objeto

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(repository).save(eq(solicitation));
    }

    @Test
    @DisplayName("renew deve aprovar solicitação válida")
    void shouldRenewApprovedSolicitation() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias
        solicitation.setJustificativa("Justificativa válida e detalhada");

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        // retorna exatamente o objeto passado no save
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.ATIVO, result.getStatus());
        assertNotNull(result.getProtocolo());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(result));
    }

    @Test
    @DisplayName("renew deve negar por justificativa insuficiente")
    void shouldRenewDeniedForShortJustification() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias
        solicitation.setJustificativa("curta"); // justificativa insuficiente

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        // retorna exatamente o objeto passado no save
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Justificativa insuficiente ou genérica", result.getNegationReason());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(result));
    }

    @Test
    @DisplayName("renew deve negar quando já existe solicitação ativa para o módulo")
    void shouldRenewDeniedForDuplicateSolicitation() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.existsByUserAndModuleAndStatus(eq(user), eq(module), eq(SolicitationStatus.ATIVO.getCode())))
                .thenReturn(true);
        // retorna exatamente o objeto passado no save
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Usuário já possui solicitação ativa para este módulo", result.getNegationReason());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).existsByUserAndModuleAndStatus(eq(user), eq(module), eq(SolicitationStatus.ATIVO.getCode()));
        verify(repository).save(eq(result));
    }

    @Test
    @DisplayName("renew deve negar quando usuário já possui acesso ativo ao módulo")
    void shouldRenewDeniedForExistingAccess() {
        // limpa acessos existentes e adiciona um acesso ativo ao mesmo módulo
        user.getAccesses().clear();
        user.addAccess(new UserModuleAccess(user, module));

        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        // retorna exatamente o objeto passado no save
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Usuário já possui acesso ativo a este módulo", result.getNegationReason());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(result));
    }

    @Test
    @DisplayName("renew deve negar quando módulo é incompatível")
    void shouldRenewDeniedForIncompatibleModule() {
        // cria um módulo já ativo no usuário
        Module other = new Module();
        other.setId(99L);
        other.setName("Aprovador Financeiro");
        user.getAccesses().clear();
        user.addAccess(new UserModuleAccess(user, other));

        // cria a solicitação original para módulo incompatível
        Module novoModulo = new Module();
        novoModulo.setId(100L);
        novoModulo.setName("Solicitante Financeiro");
        solicitation.setModule(novoModulo);
        solicitation.setProtocolo("SOL-123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        // retorna exatamente o objeto passado no save
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Módulo incompatível com outro módulo já ativo em seu perfil", result.getNegationReason());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(result));
    }

    @Test
    @DisplayName("renew deve negar quando limite de módulos ativos é atingido")
    void shouldRenewDeniedForLimitExceeded() {
        // configura solicitação original
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("SOL-123"); // protocolo definido
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setExpiresAt(LocalDateTime.now().plusDays(10)); // dentro da regra de 30 dias

        // adiciona 10 acessos simulando limite atingido
        user.getAccesses().clear();
        IntStream.range(0, 10).forEach(i -> {
            Module extra = new Module();
            extra.setId((long) (i + 100));
            extra.setName("ModuloExtra" + i);
            user.addAccess(new UserModuleAccess(user, extra));
        });

        // mocks
        when(repository.findByProtocoloWithHistory(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        // retorna exatamente o objeto passado no save
        when(repository.save(eq(solicitation))).thenReturn(solicitation); // não serve mais
        // solução correta: capturar o objeto salvo
        when(repository.save(org.mockito.ArgumentMatchers.any(AccessSolicitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // execução
        AccessSolicitation result = service.renew("SOL-123");

        // validações
        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Limite de módulos ativos atingido", result.getNegationReason());
        assertEquals(solicitation, result.getPreviousSolicitation()); // vínculo à original

        // verifica chamadas
        verify(repository).findByProtocoloWithHistory(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(result));
    }

}
