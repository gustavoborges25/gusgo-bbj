-- insert default de academia
INSERT INTO academies (id, name, email, phone, created_at, updated_at) VALUES
('2863a5ec-7c67-4326-b4b2-92db272f5518','Academia Teste', 'teste@teste.com', '44999999999',NOW(), NOW());
-- insert default de usuario (admin123)
INSERT INTO users (id, academy_id, name, email, password_hash, role, created_at, updated_at) VALUES (
    'efd386cc-2e37-45b4-8786-32531867ca16',
    '2863a5ec-7c67-4326-b4b2-92db272f5518',
    'admin',
    'admin@teste.com',
    '$2a$10$NnsM904eEMWEhKuy5Ig5Z.imXqRcRWViV7oKNzVuhPavl8TSViBse',
    'OWNER',
    NOW(),
    NOW()
);
