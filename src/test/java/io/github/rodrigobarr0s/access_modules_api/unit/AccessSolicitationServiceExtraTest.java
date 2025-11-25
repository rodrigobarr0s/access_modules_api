package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;
import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;

class AccessSolicitationServiceExtraTest {

    @Mock private AccessSolicitationRepository repository;
    @Mock private SolicitationSequenceRepository sequenceRepository;
    @Mock private UserRepository userRepository;
    @Mock private ModuleRepository moduleRepository;

    @InjectMocks private AccessSolicitationService service;

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
        when(repository.findAll(eqSpecification())).thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(
                SolicitationStatus.ATIVO, user.getId(), module.getId(), true);

        assertEquals(1, result.size());
        verify(repository).findAll(eqSpecification());
    }

    @Test
    @DisplayName("findWithFilters sem filtros deve retornar todos")
    void shouldReturnAllWithoutFilters() {
        when(repository.findAll(eqSpecification())).thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(null, null, null, null);

        assertEquals(1, result.size());
        verify(repository).findAll(eqSpecification());
    }

    @Test
    @DisplayName("findByProtocolo deve retornar solicitação autorizada")
    void shouldFindByProtocoloAuthorized() {
        when(repository.findByProtocolo(eq("SOL-123"))).thenReturn(Optional.of(solicitation));

        AccessSolicitation result = service.findByProtocolo("SOL-123");

        assertEquals(solicitation, result);
        verify(repository).findByProtocolo(eq("SOL-123"));
    }

    @Test
    @DisplayName("findByProtocolo deve lançar AccessDeniedException para usuário diferente")
    void shouldThrowAccessDeniedForDifferentUser() {
        User otherUser = new User();
        otherUser.setEmail("other@email.com");
        solicitation.setUser(otherUser);

        when(repository.findByProtocolo(eq("SOL-123"))).thenReturn(Optional.of(solicitation));

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@email.com", null));

        assertThrows(AccessDeniedException.class, () -> service.findByProtocolo("SOL-123"));
        verify(repository).findByProtocolo(eq("SOL-123"));
    }

    @Test
    @DisplayName("findByProtocolo deve lançar ResourceNotFoundException quando não encontrado")
    void shouldThrowNotFoundWhenProtocoloMissing() {
        when(repository.findByProtocolo(eq("SOL-404"))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByProtocolo("SOL-404"));
        verify(repository).findByProtocolo(eq("SOL-404"));
    }

    @Test
    @DisplayName("cancel deve atualizar status para CANCELADO e salvar")
    void shouldCancelSolicitation() {
        when(repository.findByProtocolo(eq("SOL-123"))).thenReturn(Optional.of(solicitation));

        ArgumentCaptor<AccessSolicitation> captor = ArgumentCaptor.forClass(AccessSolicitation.class);
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.cancel("SOL-123", "Motivo de cancelamento");

        assertEquals(SolicitationStatus.CANCELADO, result.getStatus());
        assertEquals("Motivo de cancelamento", result.getCancelReason());

        verify(repository).findByProtocolo(eq("SOL-123"));
        verify(repository).save(captor.capture());
        assertEquals(SolicitationStatus.CANCELADO, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("renew deve aprovar solicitação válida")
    void shouldRenewApprovedSolicitation() {
        when(repository.findByProtocolo(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.renew("SOL-123");

        assertEquals(SolicitationStatus.ATIVO, result.getStatus());
        assertNotNull(result.getProtocolo());

        verify(repository).findByProtocolo(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(solicitation));
    }

    @Test
    @DisplayName("renew deve negar solicitação inválida")
    void shouldRenewDeniedSolicitation() {
        solicitation.setJustificativa("curta"); // força negação
        when(repository.findByProtocolo(eq("SOL-123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        AccessSolicitation result = service.renew("SOL-123");

        assertEquals(SolicitationStatus.NEGADO, result.getStatus());
        assertEquals("Justificativa insuficiente ou genérica", result.getNegationReason());

        verify(repository).findByProtocolo(eq("SOL-123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(solicitation));
    }

    // Helper para evitar cast inseguro
    private Specification<AccessSolicitation> eqSpecification() {
        return argThat(spec -> spec != null);
    }
}
