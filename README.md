# Access Modules API

API para gerenciamento de usuários, módulos e incompatibilidades de acesso.  
Projeto desenvolvido em **Spring Boot 3.5.8**, com **Java 21**, **Liquibase**, **JWT**, **Spring Security**, **Spring Validation**, **Springdoc OpenAPI** e banco de dados **PostgreSQL**.

---

## 🚀 Tecnologias utilizadas
- Java 21
- Spring Boot 3.5.8
- Spring Data JPA
- Spring Security + JWT
- Spring Validation
- Liquibase (migrations)
- PostgreSQL (produção)
- H2 (testes)
- Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito + Instancio (testes unitários)
- JaCoCo (cobertura mínima 80%)

---

## 📦 Como executar com Docker

1. **Build da aplicação:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Subir containers:**
   ```bash
   docker-compose up -d
   ```

3. A aplicação estará disponível em:
   ```
   http://localhost:8080
   ```

---

## 🔑 Credenciais iniciais

Usuários populados via Liquibase:

| Email                   | Senha   | Role        |
|--------------------------|---------|-------------|
| financeiro@empresa.com   | 123456  | FINANCEIRO  |
| rh@empresa.com           | 123456  | RH          |
| operacoes@empresa.com    | 123456  | OPERACOES   |
| ti@empresa.com           | 123456  | TI          |

> ⚠️ As senhas são armazenadas criptografadas com **BCrypt**.  
> Para login, utilize o endpoint de autenticação com email e senha em texto puro.

---

## 📖 Documentação da API (Swagger)

Após subir a aplicação, acesse:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Testes

Rodar todos os testes:
```bash
mvn test
```

Gerar relatório de cobertura JaCoCo:
```bash
mvn verify
```

Abrir relatório:
```
target/site/jacoco/index.html
```

> O build falha se a cobertura for menor que **80%**.

---

## 🗂️ Estrutura de módulos

Módulos iniciais populados via Liquibase:

1. Portal do Colaborador  
2. Relatórios Gerenciais  
3. Gestão Financeira  
4. Aprovador Financeiro  
5. Solicitante Financeiro  
6. Administrador RH  
7. Colaborador RH  
8. Gestão de Estoque  
9. Compras  
10. Auditoria  

### 🔒 Incompatibilidades
- Aprovador Financeiro ↔ Solicitante Financeiro  
- Administrador RH ↔ Colaborador RH  

---

## 📌 Observações
- O projeto segue boas práticas de **DDD** e **Clean Code**.  
- Exceções são tratadas globalmente com `@ControllerAdvice`.  
- Senhas nunca são retornadas em respostas da API.  
- JWT é utilizado para autenticação e autorização.  

---

## 👨‍💻 Autor
Rodrigo Barros  

