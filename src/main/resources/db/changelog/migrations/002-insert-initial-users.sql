--liquibase formatted sql

--changeset rodrigobarr0s:insert-initial-users
--comment: insert initial users for testing

INSERT INTO users (username, password, role) VALUES
('finance_admin', '$2a$10$n7yZDJlCBrPpH.a89.WjL..4fHHACbrUkImRt5NAos27nYPuaJdBO', 'Financeiro'),
('rh_admin', '$2a$10$8zYjIqSFnkt84XeEZ3KcUeHM6z82ljmBVOW/gGm2o0SvsqHg5j7Na', 'RH'),
('ops_user', '$2a$10$AD5xMQ3y.bfMQLlHDNRDGevnqeRUl.A08kgZtpPsGZjdKNv0ALCCO', 'Operações'),
('ti_auditor', '$2a$10$XEDZv.AtLZ2GG8ivk8NFAecIfBNYGKOesQOkaB..QgpfIiwYEA8zy', 'TI');

--rollback DELETE FROM users WHERE username IN ('finance_admin','rh_admin','ops_user','ti_auditor');
