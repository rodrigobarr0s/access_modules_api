package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class AccessSolicitationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Token real já gerado para o usuário rh@empresa.com com role RH
    // Certifique-se que o secret configurado em application-test.properties é o mesmo usado para gerar esse token
    private final String tokenRh = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJmaW5hbmNlaXJvQGVtcHJlc2EuY29tIiwicm9sZSI6IkZJTkFOQ0VJUk8iLCJ1c2VySWQiOjEsImV4cCI6MTc2MzkyMzk0NX0.1QpcZZgfKGdXD7P-1-qbvbd_Uwq8OcJaEEQ-DCC7l0pkMyrHWCTqOTN9ohPTdjBv"; // coloque aqui o token válido

    @Test
    @DisplayName("Fluxo completo com segurança usando token real")
    void fullFlowTest() throws Exception {

        // 1. Criar solicitação
        AccessSolicitationRequest request = new AccessSolicitationRequest(1L, 1L, "teste integração", true);

        String response = mockMvc.perform(post("/solicitations")
                        .header("Authorization", tokenRh)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.protocolo").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String protocolo = objectMapper.readTree(response).get("protocolo").asText();

        // 2. Listar solicitações
        mockMvc.perform(get("/solicitations")
                        .header("Authorization", tokenRh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].protocolo").value(protocolo));

        // 3. Buscar por protocolo
        mockMvc.perform(get("/solicitations/" + protocolo)
                        .header("Authorization", tokenRh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value(protocolo));

        // 4. Cancelar solicitação
        mockMvc.perform(put("/solicitations/" + protocolo + "/cancel")
                        .header("Authorization", tokenRh)
                        .param("reason", "não necessário"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));

        // 5. Renovar solicitação
        mockMvc.perform(put("/solicitations/" + protocolo + "/renew")
                        .header("Authorization", tokenRh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.protocolo").exists());
    }
}
