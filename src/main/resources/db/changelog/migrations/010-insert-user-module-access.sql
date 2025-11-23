--liquibase formatted sql

--changeset rodrigobarr0s:insert-user-access-financeiro
INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES (
  (SELECT id FROM users WHERE email = 'financeiro@empresa.com'),
  (SELECT id FROM modules WHERE name = 'Gestão Financeira'),
  CURRENT_TIMESTAMP
);
--rollback DELETE FROM user_module_access WHERE user_id = (SELECT id FROM users WHERE email = 'financeiro@empresa.com');

--changeset rodrigobarr0s:insert-user-access-rh
INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES (
  (SELECT id FROM users WHERE email = 'rh@empresa.com'),
  (SELECT id FROM modules WHERE name = 'Colaborador RH'),
  CURRENT_TIMESTAMP
);
--rollback DELETE FROM user_module_access WHERE user_id = (SELECT id FROM users WHERE email = 'rh@empresa.com');

--changeset rodrigobarr0s:insert-user-access-operacoes
INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES (
  (SELECT id FROM users WHERE email = 'operacoes@empresa.com'),
  (SELECT id FROM modules WHERE name = 'Gestão de Estoque'),
  CURRENT_TIMESTAMP
);
--rollback DELETE FROM user_module_access WHERE user_id = (SELECT id FROM users WHERE email = 'operacoes@empresa.com');

--changeset rodrigobarr0s:insert-user-access-ti
INSERT INTO user_module_access (user_id, module_id, granted_at)
VALUES (
  (SELECT id FROM users WHERE email = 'ti@empresa.com'),
  (SELECT id FROM modules WHERE name = 'Auditoria'),
  CURRENT_TIMESTAMP
);
--rollback DELETE FROM user_module_access WHERE user_id = (SELECT id FROM users WHERE email = 'ti@empresa.com');
