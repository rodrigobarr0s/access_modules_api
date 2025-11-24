package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

class AccessSolicitationServiceBranchesTest {

    private final AccessSolicitationRepository repository = mock(AccessSolicitationRepository.class);
    private final SolicitationSequenceRepository sequenceRepository = mock(SolicitationSequenceRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ModuleRepository moduleRepository = mock(ModuleRepository.class);

    private final AccessSolicitationService service = new AccessSolicitationService(repository, sequenceRepository,
            userRepository, moduleRepository);

    private User buildUser(Role role) {
        return new User("user@empresa.com", "123456", role);
    }

    private Module buildModule(String name) {
        Module m = new Module();
        m.setName(name);
        return m;
    }

    @Test
    @DisplayName("Negação: justificativa inválida")
    void shouldDenyWhenJustificationInvalid() {
        User user = buildUser(Role.TI);
        Module module = buildModule("Gestão Financeira");
        String motivo = invokeValidar(user, module, "curta");
        assertEquals("Justificativa insuficiente ou genérica", motivo);
    }

    @Test
    @DisplayName("Negação: solicitação já ativa")
    void shouldDenyWhenActiveSolicitationExists() {
        when(repository.existsByUserAndModuleAndStatus(any(), any(), eq(SolicitationStatus.ATIVO.getCode())))
                .thenReturn(true);

        User user = buildUser(Role.TI);
        Module module = buildModule("Gestão Financeira");
        String motivo = invokeValidar(user, module, "Justificativa válida e longa o suficiente...");
        assertEquals("Usuário já possui solicitação ativa para este módulo", motivo);
    }

    @Test
    @DisplayName("Negação: usuário já possui acesso")
    void shouldDenyWhenUserAlreadyHasAccess() {
        User user = buildUser(Role.TI);
        Module module = buildModule("Gestão Financeira");
        user.addAccess(new UserModuleAccess(user, module));

        String motivo = invokeValidar(user, module, "Justificativa válida e longa o suficiente...");
        assertEquals("Usuário já possui acesso ativo a este módulo", motivo);
    }

    @Test
    @DisplayName("Negação: departamento sem permissão")
    void shouldDenyWhenDepartmentCannotAccessModule() {
        User user = buildUser(Role.FINANCEIRO);
        Module module = buildModule("Gestão de Estoque"); // não permitido para Financeiro

        String motivo = invokeValidar(user, module, "Justificativa válida e longa o suficiente...");
        assertEquals("Departamento sem permissão para acessar este módulo", motivo);
    }

    @Disabled
    @Test
    @DisplayName("Negação: módulo incompatível")
    void shouldDenyWhenModuleIsIncompatible() {
        User user = buildUser(Role.RH);

        // Usuário já tem acesso a "Administrador RH"
        Module ativo = buildModule("Administrador RH");
        user.addAccess(new UserModuleAccess(user, ativo));

        // Solicita "Colaborador RH" (incompatível com Administrador RH)
        Module novo = buildModule("Colaborador RH");

        // IMPORTANTE: não adicionar acesso ao 'novo' módulo, apenas ao 'ativo'
        String motivo = invokeValidar(user, novo, "Justificativa válida e longa o suficiente...");
        assertEquals("Módulo incompatível com outro módulo já ativo em seu perfil", motivo);
    }

    @Disabled
    @Test
    @DisplayName("Negação: limite de módulos atingido")
    void shouldDenyWhenLimitReached() {
        User user = buildUser(Role.FINANCEIRO);
        for (int i = 0; i < 5; i++) {
            user.addAccess(new UserModuleAccess(user, buildModule("Modulo" + i)));
        }
        Module novo = buildModule("Gestão Financeira");

        String motivo = invokeValidar(user, novo, "Justificativa válida e longa o suficiente...");
        assertEquals("Limite de módulos ativos atingido", motivo);
    }

    @Test
    @DisplayName("Aprovação: todas as regras atendidas")
    void shouldApproveWhenAllRulesPass() {
        User user = buildUser(Role.TI);
        Module module = buildModule("Gestão Financeira");

        String motivo = invokeValidar(user, module, "Justificativa válida e longa o suficiente...");
        assertNull(motivo);
    }

    // Helper para chamar validarSolicitacao via reflexão (já que é private)
    private String invokeValidar(User user, Module module, String justificativa) {
        try {
            var method = AccessSolicitationService.class
                    .getDeclaredMethod("validarSolicitacao", User.class, Module.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(service, user, module, justificativa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
