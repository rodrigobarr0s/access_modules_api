--liquibase formatted sql

--changeset rodrigobarr0s:insert-user-module-access
--comment: Vincular usuários iniciais aos módulos correspondentes

INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES
((SELECT id FROM users WHERE username = 'finance_admin'),
 (SELECT id FROM modules WHERE name = 'FINANCEIRO'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE username = 'rh_admin'),
 (SELECT id FROM modules WHERE name = 'RH'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE username = 'ops_user'),
 (SELECT id FROM modules WHERE name = 'OPERACOES'),
 CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE username = 'ti_auditor'),
 (SELECT id FROM modules WHERE name = 'TI'),
 CURRENT_TIMESTAMP);

--rollback DELETE FROM user_module_access;
