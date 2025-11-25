package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

@ExtendWith(MockitoExtension.class)
class AccessSolicitationServiceFindWithFiltersTest {

    @Mock
    private AccessSolicitationRepository repository;

    @InjectMocks
    private AccessSolicitationService service;

    @Test
    void deveFiltrarPorStatus() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(SolicitationStatus.ATIVO);

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(SolicitationStatus.ATIVO, null, null, null);

        assertEquals(1, result.size());
        assertEquals(SolicitationStatus.ATIVO, result.get(0).getStatus());

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }

    @Test
    void deveFiltrarPorUsuario() {
        AccessSolicitation solicitation = new AccessSolicitation();
        User user = new User();
        user.setId(1L);
        solicitation.setUser(user);

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(null, 1L, null, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUser().getId());

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }

    @Test
    void deveFiltrarPorModulo() {
        AccessSolicitation solicitation = new AccessSolicitation();
        Module module = new Module();
        module.setId(2L);
        solicitation.setModule(module);

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(null, null, 2L, null);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getModule().getId());

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }

    @Test
    void deveFiltrarPorUrgencia() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUrgente(true);

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(null, null, null, true);

        assertEquals(1, result.size());
        assertEquals(true, result.get(0).isUrgente()); // 🔹 usar isUrgente()

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }

    @Test
    void deveFiltrarPorTodosOsParametros() {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setStatus(SolicitationStatus.ATIVO);

        User user = new User();
        user.setId(1L);
        solicitation.setUser(user);

        Module module = new Module();
        module.setId(2L);
        solicitation.setModule(module);

        solicitation.setUrgente(true);

        when(repository.findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null)))
                .thenReturn(List.of(solicitation));

        List<AccessSolicitation> result = service.findWithFilters(SolicitationStatus.ATIVO, 1L, 2L, true);

        assertEquals(1, result.size());
        assertEquals(SolicitationStatus.ATIVO, result.get(0).getStatus());
        assertEquals(1L, result.get(0).getUser().getId());
        assertEquals(2L, result.get(0).getModule().getId());
        assertEquals(true, result.get(0).isUrgente());

        verify(repository).findAll(argThat((Specification<AccessSolicitation> spec) -> spec != null));
    }
}
