--liquibase formatted sql

--changeset rodrigobarr0s:insert-user-module-access 
--comment: Inserção de vínculos iniciais entre usuários e módulos

INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES
((SELECT id FROM users WHERE email = 'financeiro@empresa.com'),
 (SELECT id FROM modules WHERE name = 'FINANCEIRO'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'rh@empresa.com'),
 (SELECT id FROM modules WHERE name = 'RH'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'operacoes@empresa.com'),
 (SELECT id FROM modules WHERE name = 'OPERACOES'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE email = 'auditoria@empresa.com'),
 (SELECT id FROM modules WHERE name = 'TI'),
 CURRENT_TIMESTAMP);

--rollback DELETE FROM user_module_access;
