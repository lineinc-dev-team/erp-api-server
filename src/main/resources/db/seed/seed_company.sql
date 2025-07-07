INSERT INTO company (id, created_at, updated_at, created_by, updated_by, deleted, deleted_at, name)
VALUES (nextval('company_seq'), now(), now(), 'system', 'system', false, NULL, '라인공영')
ON CONFLICT (name) DO NOTHING;
