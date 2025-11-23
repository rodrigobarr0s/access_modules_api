package io.github.rodrigobarr0s.access_modules_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do OpenAPI/Swagger.
 *
 * Essa classe define:
 * - Informações básicas da API (nome e versão).
 * - Esquema de segurança do tipo Bearer JWT.
 * - Requisito de segurança global para os endpoints.
 *
 * Resultado:
 * - O Swagger UI exibirá o botão "Authorize".
 * - O usuário poderá colar o token JWT no formato "Bearer <TOKEN>".
 * - Todos os endpoints protegidos passarão a ser chamados com o header Authorization.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bean que customiza a definição OpenAPI da aplicação.
     *
     * @return objeto OpenAPI configurado com título, versão e esquema de segurança JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // Define informações básicas da API
            .info(new Info()
                .title("Access Modules API")
                .version("v0"))
            
            // Adiciona requisito de segurança global (bearerAuth)
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            
            // Declara o esquema de segurança bearerAuth (JWT)
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")          // Nome do esquema
                        .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                        .scheme("bearer")            // Usa Bearer Token
                        .bearerFormat("JWT")));      // Formato JWT
    }
}
