--liquibase formatted sql

--changeset rodrigobarr0s:create-user-module-access 
--comment: Criação da tabela de junção entre usuários e módulos

CREATE TABLE user_module_access (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_module_access PRIMARY KEY (id),
    CONSTRAINT fk_user_module_access_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_module_access_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

--rollback DROP TABLE user_module_access;
