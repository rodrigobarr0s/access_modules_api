--liquibase formatted sql

--changeset rodrigobarr0s:insert-initial-users 
--comment: Inserção de usuários iniciais com senhas BCrypt

INSERT INTO users (email, password, role) VALUES
('financeiro@empresa.com', '$2a$10$n7yZDJlCBrPpH.a89.WjL..4fHHACbrUkImRt5NAos27nYPuaJdBO', 2),
('rh@empresa.com', '$2a$10$8zYjIqSFnkt84XeEZ3KcUeHM6z82ljmBVOW/gGm2o0SvsqHg5j7Na', 3),
('operacoes@empresa.com', '$2a$10$AD5xMQ3y.bfMQLlHDNRDGevnqeRUl.A08kgZtpPsGZjdKNv0ALCCO', 4),
('auditoria@empresa.com', '$2a$10$XEDZv.AtLZ2GG8ivk8NFAecIfBNYGKOesQOkaB..QgpfIiwYEA8zy', 5);

--rollback DELETE FROM users WHERE email IN ('financeiro@empresa.com','rh@empresa.com','operacoes@empresa.com','auditoria@empresa.com');
