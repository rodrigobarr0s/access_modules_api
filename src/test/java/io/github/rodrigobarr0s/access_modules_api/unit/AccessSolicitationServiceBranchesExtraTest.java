package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

@ExtendWith(MockitoExtension.class)
class AccessSolicitationServiceBranchesExtraTest {

    @Mock
    private AccessSolicitationRepository repository;

    @InjectMocks
    private AccessSolicitationService service;

    @ParameterizedTest
    @MethodSource("cenariosExtras")
    void deveCobrirBranchesExtras(User user, Module module, String justificativa,
                                  boolean existsActive, String expectedReason) throws Exception {

        if (justificativa != null && justificativa.length() >= 20) {
            when(repository.existsByUserAndModuleAndStatus(eq(user), eq(module),
                    eq(SolicitationStatus.ATIVO.getCode())))
                    .thenReturn(existsActive);
        }

        var method = AccessSolicitationService.class.getDeclaredMethod(
                "validarSolicitacao", User.class, Module.class, String.class);
        method.setAccessible(true);
        String motivo = (String) method.invoke(service, user, module, justificativa);

        assertEquals(expectedReason, motivo);

        if (justificativa != null && justificativa.length() >= 20) {
            verify(repository).existsByUserAndModuleAndStatus(eq(user), eq(module),
                    eq(SolicitationStatus.ATIVO.getCode()));
        }
    }

    static Stream<Arguments> cenariosExtras() {
        return Stream.of(
            // RH autorizado
            Arguments.of(criarUsuario(Role.RH), criarModulo("Administrador RH"),
                    "Justificativa válida com mais de 20 caracteres", false, null),

            // Operações autorizado
            Arguments.of(criarUsuario(Role.OPERACOES), criarModulo("Gestão de Estoque"),
                    "Justificativa válida com mais de 20 caracteres", false, null),

            // Default autorizado
            Arguments.of(criarUsuario(Role.ADMIN), criarModulo("Portal do Colaborador"),
                    "Justificativa válida com mais de 20 caracteres", false, null),

            // Incompatibilidade RH
            Arguments.of(criarUsuarioComModulo("Administrador RH", Role.RH),
                    criarModulo("Colaborador RH"), "Justificativa válida com mais de 20 caracteres", false,
                    "Módulo incompatível com outro módulo já ativo em seu perfil"),

            // Incompatibilidade RH inverso
            Arguments.of(criarUsuarioComModulo("Colaborador RH", Role.RH),
                    criarModulo("Administrador RH"), "Justificativa válida com mais de 20 caracteres", false,
                    "Módulo incompatível com outro módulo já ativo em seu perfil")
        );
    }

    private static User criarUsuario(Role role) {
        User user = new User();
        user.setRole(role);
        return user;
    }

    private static User criarUsuarioComModulo(String nomeModulo, Role role) {
        User user = new User();
        user.setRole(role);
        user.addAccess(new UserModuleAccess(user, criarModulo(nomeModulo)));
        return user;
    }

    private static Module criarModulo(String nome) {
        Module module = new Module();
        module.setName(nome);
        return module;
    }
}
