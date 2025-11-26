package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import io.github.rodrigobarr0s.access_modules_api.entity.Module;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.rodrigobarr0s.access_modules_api.controller.AccessSolicitationController;
import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import io.github.rodrigobarr0s.access_modules_api.dto.CancelRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.security.JwtFilter;
import io.github.rodrigobarr0s.access_modules_api.security.SecurityConfig;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

@WebMvcTest(controllers = AccessSolicitationController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class, JwtFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class AccessSolicitationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AccessSolicitationService service;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /solicitations deve criar solicitações e retornar 201")
        void deveCriarSolicitacoes() throws Exception {
                AccessSolicitationRequest request = new AccessSolicitationRequest();
                request.setModuleIds(List.of(1L));
                request.setJustificativa("Necessário acesso válido com mais de vinte caracteres");
                request.setUrgente(true);

                AccessSolicitation solicitation = new AccessSolicitation();
                solicitation.setProtocolo("PROTO123");
                solicitation.setStatus(SolicitationStatus.ATIVO);

                // Usa argThat para casar com qualquer objeto válido em vez de eq(request)
                Mockito.when(service.create(ArgumentMatchers
                                .<AccessSolicitationRequest>argThat(r -> r.getModuleIds().equals(List.of(1L)) &&
                                                r.getJustificativa().equals(
                                                                "Necessário acesso válido com mais de vinte caracteres")
                                                &&
                                                r.isUrgente())))
                                .thenReturn(List.of(solicitation));

                mockMvc.perform(post("/solicitations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$[0].protocolo").value("PROTO123"))
                                .andExpect(jsonPath("$[0].status").value("ATIVO"));
        }

        @Test
        @DisplayName("GET /solicitations deve listar solicitações com filtros")
        void deveListarSolicitacoes() throws Exception {
                AccessSolicitation solicitation = new AccessSolicitation();
                solicitation.setProtocolo("PROTO456");
                solicitation.setStatus(SolicitationStatus.ATIVO);

                // Corrige: cria um módulo e associa
                Module module = new Module();
                module.setId(1L);
                module.setName("Gestão Financeira");
                solicitation.setModule(module);

                Pageable pageable = PageRequest.of(0, 10);
                Page<AccessSolicitation> page = new PageImpl<>(List.of(solicitation), pageable, 1);

                Mockito.when(service.findWithFilters(
                                eq(SolicitationStatus.ATIVO),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                ArgumentMatchers.<Pageable>argThat(p -> p.getPageSize() == 10)))
                                .thenReturn(page);

                mockMvc.perform(get("/solicitations")
                                .param("status", SolicitationStatus.ATIVO.name()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].protocolo").value("PROTO456"))
                                .andExpect(jsonPath("$.content[0].moduleId").value(1))
                                .andExpect(jsonPath("$.content[0].moduleName").value("Gestão Financeira"));
        }

        @Test
        @DisplayName("GET /solicitations/{protocolo} deve buscar solicitação por protocolo")
        void deveBuscarPorProtocolo() throws Exception {
                AccessSolicitation solicitation = new AccessSolicitation();
                solicitation.setProtocolo("PROTO789");
                solicitation.setStatus(SolicitationStatus.ATIVO);

                // Corrige: cria um módulo e associa
                io.github.rodrigobarr0s.access_modules_api.entity.Module module = new io.github.rodrigobarr0s.access_modules_api.entity.Module();
                module.setId(1L);
                module.setName("Gestão Financeira");
                solicitation.setModule(module);

                // Corrige: cria um usuário e associa
                io.github.rodrigobarr0s.access_modules_api.entity.User user = new io.github.rodrigobarr0s.access_modules_api.entity.User();
                user.setId(10L);
                user.setEmail("user@email.com");
                solicitation.setUser(user);

                Mockito.when(service.findByProtocolo(eq("PROTO789"))).thenReturn(solicitation);

                mockMvc.perform(get("/solicitations/PROTO789"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.protocolo").value("PROTO789"))
                                .andExpect(jsonPath("$.status").value("ATIVO"))
                                .andExpect(jsonPath("$.moduleId").value(1))
                                .andExpect(jsonPath("$.moduleName").value("Gestão Financeira"))
                                .andExpect(jsonPath("$.userId").value(10))
                                .andExpect(jsonPath("$.userEmail").value("user@email.com"));
        }

        @Test
        @DisplayName("PATCH /solicitations/{protocolo}/cancel deve cancelar solicitação")
        void deveCancelarSolicitacao() throws Exception {
                CancelRequest cancelRequest = new CancelRequest();
                cancelRequest.setReason("Motivo válido para cancelamento"); // >= 10 caracteres

                AccessSolicitation solicitation = new AccessSolicitation();
                solicitation.setProtocolo("PROTO999");
                solicitation.setStatus(SolicitationStatus.CANCELADO);

                // Corrige: cria um módulo e associa
                io.github.rodrigobarr0s.access_modules_api.entity.Module module = new io.github.rodrigobarr0s.access_modules_api.entity.Module();
                module.setId(1L);
                module.setName("Gestão Financeira");
                solicitation.setModule(module);

                // Corrige: cria um usuário e associa
                io.github.rodrigobarr0s.access_modules_api.entity.User user = new io.github.rodrigobarr0s.access_modules_api.entity.User();
                user.setId(10L);
                user.setEmail("user@email.com");
                solicitation.setUser(user);

                Mockito.when(service.cancel(eq("PROTO999"), eq("Motivo válido para cancelamento")))
                                .thenReturn(solicitation);

                mockMvc.perform(patch("/solicitations/PROTO999/cancel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cancelRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.protocolo").value("PROTO999"))
                                .andExpect(jsonPath("$.status").value("CANCELADO"))
                                .andExpect(jsonPath("$.moduleId").value(1))
                                .andExpect(jsonPath("$.moduleName").value("Gestão Financeira"))
                                .andExpect(jsonPath("$.userId").value(10))
                                .andExpect(jsonPath("$.userEmail").value("user@email.com"));
        }

        @Test
        @DisplayName("PATCH /solicitations/{protocolo}/renew deve renovar solicitação")
        void deveRenovarSolicitacao() throws Exception {
                AccessSolicitation solicitation = new AccessSolicitation();
                solicitation.setProtocolo("PROTO321");
                solicitation.setStatus(SolicitationStatus.ATIVO);

                // Corrige: cria um módulo e associa
                io.github.rodrigobarr0s.access_modules_api.entity.Module module = new io.github.rodrigobarr0s.access_modules_api.entity.Module();
                module.setId(1L);
                module.setName("Gestão Financeira");
                solicitation.setModule(module);

                // Corrige: cria um usuário e associa
                io.github.rodrigobarr0s.access_modules_api.entity.User user = new io.github.rodrigobarr0s.access_modules_api.entity.User();
                user.setId(10L);
                user.setEmail("user@email.com");
                solicitation.setUser(user);

                Mockito.when(service.renew(eq("PROTO321"))).thenReturn(solicitation);

                mockMvc.perform(patch("/solicitations/PROTO321/renew"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.protocolo").value("PROTO321"))
                                .andExpect(jsonPath("$.status").value("ATIVO"))
                                .andExpect(jsonPath("$.moduleId").value(1))
                                .andExpect(jsonPath("$.moduleName").value("Gestão Financeira"))
                                .andExpect(jsonPath("$.userId").value(10))
                                .andExpect(jsonPath("$.userEmail").value("user@email.com"));
        }

}
