package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

@ExtendWith(MockitoExtension.class)
class AccessSolicitationServiceTest {

    @Mock
    private AccessSolicitationRepository repository;

    @Mock
    private SolicitationSequenceRepository sequenceRepository;

    @InjectMocks
    private AccessSolicitationService service;

    @Test
    @DisplayName("Deve criar solicitação com protocolo e status PENDING")
    void testCreateSetsProtocoloAndPendingStatus() {
        AccessSolicitation solicitation = new AccessSolicitation();
        when(sequenceRepository.getNextSequenceValue()).thenReturn(1L);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessSolicitation created = service.create(solicitation);

        assertNotNull(created.getProtocolo());
        assertEquals(SolicitationStatus.PENDING, created.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar solicitação por protocolo")
    void testFindByProtocoloThrowsWhenNotFound() {
        when(repository.findByProtocolo("X")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.findByProtocolo("X"));
    }

    @Test
    @DisplayName("Deve aprovar solicitação e alterar status para APPROVED")
    void testApproveChangesStatusToApproved() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-1");
        solicitation.setStatus(SolicitationStatus.PENDING);

        when(repository.findByProtocolo("SOL-1")).thenReturn(Optional.of(solicitation));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessSolicitation approved = service.approve("SOL-1");

        assertEquals(SolicitationStatus.APPROVED, approved.getStatus());
    }

    @Test
    @DisplayName("Deve rejeitar solicitação e definir motivo")
    void testRejectSetsStatusRejectedAndReason() {
        AccessSolicitation solicitation = new AccessSolicitation();
        when(repository.findByProtocolo("SOL-2")).thenReturn(Optional.of(solicitation));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessSolicitation rejected = service.reject("SOL-2", "Motivo");

        assertEquals(SolicitationStatus.REJECTED, rejected.getStatus());
        assertEquals("Motivo", rejected.getCancelReason());
    }

    @Test
    @DisplayName("Deve cancelar solicitação e definir motivo")
    void testCancelSetsStatusCanceledAndReason() {
        AccessSolicitation solicitation = new AccessSolicitation();
        when(repository.findByProtocolo("SOL-3")).thenReturn(Optional.of(solicitation));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessSolicitation canceled = service.cancel("SOL-3", "Cancelado");

        assertEquals(SolicitationStatus.CANCELED, canceled.getStatus());
        assertEquals("Cancelado", canceled.getCancelReason());
    }

    @Test
    @DisplayName("Deve renovar solicitação, atualizar expiresAt e gerar novo protocolo")
    void testRenewUpdatesExpiresAtAndProtocolo() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("SOL-4");

        when(repository.findByProtocolo("SOL-4")).thenReturn(Optional.of(solicitation));
        when(sequenceRepository.getNextSequenceValue()).thenReturn(10L);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessSolicitation renewed = service.renew("SOL-4");

        assertEquals(SolicitationStatus.PENDING, renewed.getStatus());
        assertNotNull(renewed.getExpiresAt());
        assertTrue(renewed.getProtocolo().startsWith("SOL-"));
    }

    @Test
    @DisplayName("Deve chamar repositório ao buscar por status")
    void testFindByStatusCallsRepository() {
        when(repository.findByStatus(2)).thenReturn(List.of(new AccessSolicitation()));

        List<AccessSolicitation> result = service.findByStatus(SolicitationStatus.APPROVED);

        assertEquals(1, result.size());
        verify(repository).findByStatus(2);
    }

    @Test
    @DisplayName("Deve aplicar todos os filtros corretamente")
    void testFindWithFiltersFiltersCorrectly() {
        User user = new User();
        user.setId(1L);
        Module module = new Module();
        module.setId(2L);

        AccessSolicitation s1 = new AccessSolicitation();
        s1.setUser(user);
        s1.setModule(module);
        s1.setStatus(SolicitationStatus.PENDING);
        s1.setUrgente(true);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(SolicitationStatus.PENDING, 1L, 2L, true);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve filtrar por status")
    void testFindWithFiltersByStatus() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setStatus(SolicitationStatus.PENDING);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(SolicitationStatus.PENDING, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve filtrar por userId")
    void testFindWithFiltersByUserId() {
        User user = new User();
        user.setId(1L);

        AccessSolicitation s1 = new AccessSolicitation();
        s1.setUser(user);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(null, 1L, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve filtrar por moduleId")
    void testFindWithFiltersByModuleId() {
        Module module = new Module();
        module.setId(2L);

        AccessSolicitation s1 = new AccessSolicitation();
        s1.setModule(module);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(null, null, 2L, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve filtrar por urgência")
    void testFindWithFiltersByUrgente() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setUrgente(true);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(null, null, null, true);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar todos quando filtros são nulos")
    void testFindWithFiltersAllNullReturnsAll() {
        AccessSolicitation s1 = new AccessSolicitation();
        s1.setStatus(SolicitationStatus.PENDING);

        when(repository.findAll()).thenReturn(List.of(s1));

        List<AccessSolicitation> result = service.findWithFilters(null, null, null, null);

        assertEquals(1, result.size());
    }
}
