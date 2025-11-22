package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Deve permitir acesso com token válido")
    void shouldAllowAccessWithValidToken() throws Exception {
        String token = jwtUtil.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        mockMvc.perform(get("/api/protected")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve negar acesso sem token")
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve negar acesso com token inválido")
    void shouldDenyAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/protected")
                .header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve negar acesso com token expirado")
    void shouldDenyAccessWithExpiredToken() throws Exception {
        JwtUtil shortLivedJwt = new JwtUtil("mySecretKeyWith32CharsMinimumLength123", 1);
        String token = shortLivedJwt.generateToken("rodrigo@empresa.com", Role.ADMIN.name(), 1L);

        Thread.sleep(5); // espera expirar

        mockMvc.perform(get("/api/protected")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}

