package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;
import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

@ExtendWith(MockitoExtension.class)
class AccessSolicitationServiceBranchesTest {

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

    @ParameterizedTest
    @MethodSource("cenariosSolicitacao")
    void deveCobrirTodosOsBranches(User user, Module module, String justificativa,
            boolean existsActive, String expectedReason) {
        // Só stubba o repositório se a justificativa for válida
        if (justificativa != null && justificativa.length() >= 20 && justificativa.length() <= 500) {
            when(repository.existsByUserAndModuleAndStatus(eq(user), eq(module),
                    eq(SolicitationStatus.ATIVO.getCode())))
                    .thenReturn(existsActive);
        }

        // Act
        String motivo = invokeValidarSolicitacao(user, module, justificativa);

        // Assert
        assertEquals(expectedReason, motivo);

        // Verifica interação apenas quando esperado
        if (justificativa != null && justificativa.length() >= 20 && justificativa.length() <= 500) {
            verify(repository).existsByUserAndModuleAndStatus(eq(user), eq(module),
                    eq(SolicitationStatus.ATIVO.getCode()));
        } else {
            verifyNoInteractions(repository);
        }
    }

    private String invokeValidarSolicitacao(User user, Module module, String justificativa) {
        try {
            var method = AccessSolicitationService.class.getDeclaredMethod(
                    "validarSolicitacao", User.class, Module.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(service, user, module, justificativa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Arguments> cenariosSolicitacao() {
        User userBase = new User();
        userBase.setRole(Role.FINANCEIRO);

        Module moduloFinanceiro = new Module();
        moduloFinanceiro.setName("Gestão Financeira");

        Module moduloEstoque = new Module();
        moduloEstoque.setName("Gestão de Estoque");

        return Stream.of(
                // Justificativa inválida
                Arguments.of(userBase, moduloFinanceiro, "curta", false,
                        "Justificativa insuficiente ou genérica"),
                // Solicitação ativa já existe
                Arguments.of(userBase, moduloFinanceiro, "Justificativa válida com mais de 20 caracteres", true,
                        "Usuário já possui solicitação ativa para este módulo"),
                // Usuário já possui acesso ativo
                Arguments.of(criarUsuarioComAcesso(moduloFinanceiro, Role.FINANCEIRO), moduloFinanceiro,
                        "Justificativa válida com mais de 20 caracteres", false,
                        "Usuário já possui acesso ativo a este módulo"),
                // Departamento sem permissão
                Arguments.of(userBase, moduloEstoque, "Justificativa válida com mais de 20 caracteres", false,
                        "Departamento sem permissão para acessar este módulo"),
                // Módulo incompatível (usuário tem Aprovador, solicita Solicitan­te) → Role.TI
                Arguments.of(criarUsuarioComModuloIncompativel(Role.TI),
                        criarModulo("Solicitante Financeiro"), "Justificativa válida com mais de 20 caracteres", false,
                        "Módulo incompatível com outro módulo já ativo em seu perfil"),
                // Caso aprovado
                Arguments.of(userBase, moduloFinanceiro, "Justificativa válida com mais de 20 caracteres", false,
                        null));
    }

    private static User criarUsuarioComAcesso(Module module, Role role) {
        User user = new User();
        user.setRole(role);
        user.addAccess(new UserModuleAccess(user, module));
        return user;
    }

    private static User criarUsuarioComModuloIncompativel(Role role) {
        User user = new User();
        user.setRole(role);
        // Usuário tem acesso ao "Aprovador Financeiro"
        user.addAccess(new UserModuleAccess(user, criarModulo("Aprovador Financeiro")));
        return user;
    }

    private static Module criarModulo(String nome) {
        Module module = new Module();
        module.setName(nome);
        return module;
    }
}
