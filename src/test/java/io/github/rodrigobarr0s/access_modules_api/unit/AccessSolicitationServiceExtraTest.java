package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

@ExtendWith(MockitoExtension.class)
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

    @Test
    void deveFiltrarPorStatusEUsuario() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitation.setUser(new User());

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(SolicitationStatus.ATIVO, 1L, null, null);

        assertEquals(1, result.size());
        assertEquals(SolicitationStatus.ATIVO, result.get(0).getStatus());

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }

    @Test
    void deveRetornarSolicitacaoPorProtocoloQuandoAutorizado() {
        User user = new User();
        user.setEmail("teste@dominio.com");
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);

        when(repository.findByProtocolo(eq("PROTO123"))).thenReturn(Optional.of(solicitation));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@dominio.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        AccessSolicitation result = service.findByProtocolo("PROTO123");

        assertEquals(user, result.getUser());
        verify(repository).findByProtocolo(eq("PROTO123"));
    }

    @Test
    void deveLancarAccessDeniedQuandoUsuarioNaoAutorizado() {
        User user = new User();
        user.setEmail("outro@dominio.com");
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);

        when(repository.findByProtocolo(eq("PROTO123"))).thenReturn(Optional.of(solicitation));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@dominio.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class, () -> service.findByProtocolo("PROTO123"));
        verify(repository).findByProtocolo(eq("PROTO123"));
    }

    @Test
    void deveLancarResourceNotFoundQuandoSolicitacaoNaoExiste() {
        when(repository.findByProtocolo(eq("PROTO123"))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByProtocolo("PROTO123"));
        verify(repository).findByProtocolo(eq("PROTO123"));
    }

    @Test
    void deveCancelarSolicitacao() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");
        User user = new User();
        user.setEmail("teste@dominio.com");
        solicitation.setUser(user);

        when(repository.findByProtocolo(eq("PROTO123"))).thenReturn(Optional.of(solicitation));
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        // Configura autenticação no contexto
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@dominio.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        AccessSolicitation result = service.cancel("PROTO123", "Motivo de cancelamento");

        assertEquals(SolicitationStatus.CANCELADO, result.getStatus());
        assertEquals("Motivo de cancelamento", result.getCancelReason());
        assertNotNull(result.getUpdatedAt());

        verify(repository).findByProtocolo(eq("PROTO123"));
        verify(repository).save(eq(solicitation));
    }

    @Test
    void deveRenovarSolicitacao() {
        User user = new User();
        user.setEmail("teste@dominio.com");
        user.setRole(Role.FINANCEIRO); // 🔹 define o role para evitar NPE

        Module module = new Module();
        module.setName("Gestão Financeira");

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setJustificativa("Justificativa válida com mais de 20 caracteres");

        when(repository.findByProtocolo(eq("PROTO123"))).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.save(eq(solicitation))).thenReturn(solicitation);

        // Configura autenticação no contexto
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@dominio.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        AccessSolicitation result = service.renew("PROTO123");

        assertEquals(SolicitationStatus.ATIVO, result.getStatus());
        assertNotNull(result.getExpiresAt());
        assertNotEquals("PROTO123", result.getProtocolo());

        verify(repository).findByProtocolo(eq("PROTO123"));
        verify(sequenceRepository).getNextSequenceValue();
        verify(repository).save(eq(solicitation));
    }

}
