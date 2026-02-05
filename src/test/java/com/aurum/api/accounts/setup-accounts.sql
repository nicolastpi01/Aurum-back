-- Limpieza
DELETE FROM accounts;
DELETE FROM users;

-- Usuarios con ID explícito
INSERT INTO users (id, mail, password_hash, role, status, created_at, updated_at) 
VALUES (1, 'userA@aurum.com', 'hashA', 'USER', 'ACTIVE', NOW(), NOW());
INSERT INTO users (id, mail, password_hash, role, status, created_at, updated_at) 
VALUES (2, 'userB@aurum.com', 'hashB', 'USER', 'ACTIVE', NOW(), NOW());

-- Cuentas con ID explícito (así H2 no tiene que adivinar)
INSERT INTO accounts (id, user_id, currency, status, balance, created_at, updated_at) 
VALUES (101, 1, 'ARS', 'ACTIVE', 5000.0, NOW(), NOW());

INSERT INTO accounts (id, user_id, currency, status, balance, created_at, updated_at) 
VALUES (102, 1, 'USD', 'ACTIVE', 100.0, NOW(), NOW());

INSERT INTO accounts (id, user_id, currency, status, balance, created_at, updated_at) 
VALUES (201, 2, 'ARS', 'ACTIVE', 1500.0, NOW(), NOW());