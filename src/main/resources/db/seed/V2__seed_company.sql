INSERT INTO company (id, name, is_active, created_at, updated_at)
VALUES (
           nextval('company_seq'),
           '라인공영',
           true,
           now(),
           now()
       );