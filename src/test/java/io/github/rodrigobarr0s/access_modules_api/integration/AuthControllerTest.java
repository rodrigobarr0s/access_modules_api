package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.rodrigobarr0s.access_modules_api.controller.AuthController;
import io.github.rodrigobarr0s.access_modules_api.dto.LoginRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.User;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import io.github.rodrigobarr0s.access_modules_api.security.JwtFilter;
import io.github.rodrigobarr0s.access_modules_api.security.SecurityConfig;
import io.github.rodrigobarr0s.access_modules_api.security.util.JwtUtil;
import io.github.rodrigobarr0s.access_modules_api.service.UserService;

@WebMvcTest(controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { SecurityConfig.class, JwtFilter.class }
    ))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /auth/login deve autenticar usuário válido e retornar token")
    void deveAutenticarUsuarioValido() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "123456");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);

        Mockito.when(userService.findByEmail("user@email.com")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId()))
               .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.email").value("user@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 401 se usuário não encontrado")
    void deveFalharUsuarioNaoEncontrado() throws Exception {
        LoginRequest request = new LoginRequest("naoexiste@email.com", "123456");

        Mockito.when(userService.findByEmail("naoexiste@email.com")).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 401 se senha inválida")
    void deveFalharSenhaInvalida() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "wrongpass");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);

        Mockito.when(userService.findByEmail("user@email.com")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Credenciais inválidas"));
    }
}
