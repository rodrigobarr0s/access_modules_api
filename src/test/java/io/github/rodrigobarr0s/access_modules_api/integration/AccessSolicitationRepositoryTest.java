package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
class AccessSolicitationRepositoryTest {

    @Autowired
    private AccessSolicitationRepository solicitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Test
    @DisplayName("Deve salvar e buscar solicitação por protocolo")
    void deveSalvarEBuscarPorProtocolo() {
        User user = userRepository.save(new User("user@test.com", "123456", Role.ADMIN));
        Module module = moduleRepository.save(new Module("Financeiro", "Gestão financeira"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("PROTO123");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitationRepository.save(solicitation);

        Optional<AccessSolicitation> encontrado = solicitationRepository.findByProtocolo("PROTO123");

        assertTrue(encontrado.isPresent());
        assertEquals("PROTO123", encontrado.get().getProtocolo());
        assertEquals(user.getEmail(), encontrado.get().getUser().getEmail());
        assertEquals(module.getName(), encontrado.get().getModule().getName());
    }

    @Test
    @DisplayName("Deve validar existência de solicitação ativa por usuário, módulo e status")
    void deveValidarExistenciaDeSolicitacaoAtiva() {
        User user = userRepository.save(new User("user2@test.com", "abcdef", Role.FINANCEIRO));
        Module module = moduleRepository.save(new Module("RH", "Recursos Humanos"));

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setUser(user);
        solicitation.setModule(module);
        solicitation.setProtocolo("PROTO456");
        solicitation.setStatus(SolicitationStatus.ATIVO);
        solicitationRepository.save(solicitation);

        boolean exists = solicitationRepository.existsByUserAndModuleAndStatus(
                user, module, SolicitationStatus.ATIVO.getCode());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve encontrar solicitação inexistente por protocolo")
    void naoDeveEncontrarSolicitacaoInexistente() {
        Optional<AccessSolicitation> encontrado = solicitationRepository.findByProtocolo("PROTO999");
        assertTrue(encontrado.isEmpty());
    }
}
