package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.rodrigobarr0s.access_modules_api.controller.AccessSolicitationController;
import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import io.github.rodrigobarr0s.access_modules_api.dto.CancelRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.security.SecurityConfig;
import io.github.rodrigobarr0s.access_modules_api.security.JwtFilter;
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
    @DisplayName("POST /solicitations deve criar solicitação e retornar 201")
    void deveCriarSolicitacao() throws Exception {
        AccessSolicitationRequest request = new AccessSolicitationRequest();
        request.setModuleId(1L);
        request.setJustificativa("Necessário acesso");
        request.setUrgente(true);

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO123");
        solicitation.setStatus(SolicitationStatus.ATIVO); // ✅ status definido

        Mockito.when(service.create(Mockito.any())).thenReturn(solicitation);

        mockMvc.perform(post("/solicitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.protocolo").value("PROTO123"));
    }

    @Test
    @DisplayName("GET /solicitations deve listar solicitações com filtros")
    void deveListarSolicitacoes() throws Exception {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO456");
        solicitation.setStatus(SolicitationStatus.ATIVO); // ✅ status definido

        Mockito.when(service.findWithFilters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(solicitation));

        mockMvc.perform(get("/solicitations")
                .param("status", SolicitationStatus.ATIVO.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].protocolo").value("PROTO456"));
    }

    @Test
    @DisplayName("GET /solicitations/{protocolo} deve buscar solicitação por protocolo")
    void deveBuscarPorProtocolo() throws Exception {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO789");
        solicitation.setStatus(SolicitationStatus.ATIVO); // ✅ status definido

        Mockito.when(service.findByProtocolo("PROTO789")).thenReturn(solicitation);

        mockMvc.perform(get("/solicitations/PROTO789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value("PROTO789"));
    }

    @Test
    @DisplayName("PATCH /solicitations/{protocolo}/cancel deve cancelar solicitação")
    void deveCancelarSolicitacao() throws Exception {
        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setReason("Não preciso mais");

        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO999");
        solicitation.setStatus(SolicitationStatus.CANCELADO); // ✅ status definido

        Mockito.when(service.cancel(Mockito.eq("PROTO999"), Mockito.anyString())).thenReturn(solicitation);

        mockMvc.perform(patch("/solicitations/PROTO999/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value("PROTO999"));
    }

    @Test
    @DisplayName("PATCH /solicitations/{protocolo}/renew deve renovar solicitação")
    void deveRenovarSolicitacao() throws Exception {
        AccessSolicitation solicitation = new AccessSolicitation();
        solicitation.setProtocolo("PROTO321");
        solicitation.setStatus(SolicitationStatus.ATIVO); // ✅ status definido

        Mockito.when(service.renew("PROTO321")).thenReturn(solicitation);

        mockMvc.perform(patch("/solicitations/PROTO321/renew"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value("PROTO321"));
    }
}
