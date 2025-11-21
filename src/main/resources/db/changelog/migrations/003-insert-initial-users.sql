--liquibase formatted sql

--changeset rodrigobarr0s:insert-initial-users 
--comment: Inserção de usuários iniciais com senhas BCrypt

INSERT INTO users (username, password, role) VALUES
('finance_admin', '$2a$10$n7yZDJlCBrPpH.a89.WjL..4fHHACbrUkImRt5NAos27nYPuaJdBO', 2),
('rh_admin', '$2a$10$8zYjIqSFnkt84XeEZ3KcUeHM6z82ljmBVOW/gGm2o0SvsqHg5j7Na', 3),
('ops_user', '$2a$10$AD5xMQ3y.bfMQLlHDNRDGevnqeRUl.A08kgZtpPsGZjdKNv0ALCCO', 4),
('ti_auditor', '$2a$10$XEDZv.AtLZ2GG8ivk8NFAecIfBNYGKOesQOkaB..QgpfIiwYEA8zy', 5);

--rollback DELETE FROM users WHERE username IN ('finance_admin','rh_admin','ops_user','ti_auditor');
