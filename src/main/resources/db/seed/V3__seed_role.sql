-- Role 테이블에'관리자' 역할 추가
INSERT INTO role
(id,
 created_at,
 updated_at,
 created_by,
 updated_by,
 deleted,
 NAME)
VALUES (1,
        NOW(),
        NOW(),
        'system',
        'system',
        FALSE,
        '관리자')
