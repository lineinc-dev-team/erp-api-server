-- Roles 테이블에 '관리자' 역할 추가
INSERT INTO roles
(id,
 created_at,
 updated_at,
 created_by,
 updated_by,
 deleted,
 name)
VALUES (1,
        NOW(),
        NOW(),
        'system',
        'system',
        FALSE,
        '관리자')
ON CONFLICT (name) DO NOTHING;