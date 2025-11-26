package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.rodrigobarr0s.access_modules_api.controller.ModuleController;
import io.github.rodrigobarr0s.access_modules_api.dto.ModuleAvailableResponse;
import io.github.rodrigobarr0s.access_modules_api.security.JwtFilter;
import io.github.rodrigobarr0s.access_modules_api.security.SecurityConfig;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;

@WebMvcTest(controllers = ModuleController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { SecurityConfig.class, JwtFilter.class }
    ))
@AutoConfigureMockMvc(addFilters = false)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModuleService service;

    @Test
    @DisplayName("GET /modules/available deve retornar lista de módulos disponíveis")
    void deveListarModulosDisponiveis() throws Exception {
        // prepara resposta mockada
        ModuleAvailableResponse response = new ModuleAvailableResponse(
                1L,
                "Gestão Financeira",
                "Módulo para controle financeiro",
                List.of("Financeiro", "Contabilidade"),
                true,
                List.of("Gestão de Estoque")
        );

        when(service.listAvailableModules()).thenReturn(List.of(response));

        // executa requisição
        mockMvc.perform(get("/modules/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gestão Financeira"))
                .andExpect(jsonPath("$[0].description").value("Módulo para controle financeiro"))
                .andExpect(jsonPath("$[0].allowedDepartments[0]").value("Financeiro"))
                .andExpect(jsonPath("$[0].allowedDepartments[1]").value("Contabilidade"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[0].incompatibleModules[0]").value("Gestão de Estoque"));
    }

    @Test
    @DisplayName("GET /modules/available deve retornar lista vazia quando não há módulos")
    void deveRetornarListaVazia() throws Exception {
        when(service.listAvailableModules()).thenReturn(List.of());

        mockMvc.perform(get("/modules/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
