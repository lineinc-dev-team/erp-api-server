-- 관리자 유저에 ADMIN 권한 부여 (role_id = 1 이라고 가정)
INSERT INTO users_roles (users_id, roles_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;