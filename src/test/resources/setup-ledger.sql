-- Limpiamos tablas para evitar colisiones
DELETE FROM ledger_entries;
DELETE FROM accounts;
DELETE FROM users;

-- Usuario 1 y su Cuenta
INSERT INTO users (id, mail, password_hash, role, status, created_at, updated_at) 
VALUES (1, 'nico@aurum.com', 'hash', 'USER', 'ACTIVE', NOW(), NOW());

INSERT INTO accounts (id, user_id, currency, status, balance, created_at, updated_at) 
VALUES (1, 1, 'ARS', 'ACTIVE', 5000.0, NOW(), NOW());

-- Usuario 2 y su Cuenta (Para probar seguridad)
INSERT INTO users (id, mail, password_hash, role, status, created_at, updated_at) 
VALUES (2, 'test@aurum.com', 'hash', 'USER', 'ACTIVE', NOW(), NOW());

INSERT INTO accounts (id, user_id, currency, status, balance, created_at, updated_at) 
VALUES (2, 2, 'ARS', 'ACTIVE', 100.0, NOW(), NOW());

-- Movimientos para la Cuenta 1
INSERT INTO ledger_entries (account_id, entry_type, amount, currency, description, balance_after, created_at)
VALUES (1, 'CREDIT', 1000.0, 'ARS', 'Sueldo', 1000.0, NOW());

INSERT INTO ledger_entries (account_id, entry_type, amount, currency, description, balance_after, created_at)
VALUES (1, 'DEBIT', 200.0, 'ARS', 'Supermercado', 800.0, NOW());

INSERT INTO ledger_entries (account_id, entry_type, amount, currency, description, balance_after, created_at)
VALUES (1, 'CREDIT', 500.0, 'ARS', 'Transferencia recibida', 1300.0, NOW());