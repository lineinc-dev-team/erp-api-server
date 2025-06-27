-- 라인공영 회사 정보 추가 (id는 시퀀스 또는 수동 지정)
INSERT INTO company (id,
                     created_at,
                     updated_at,
                     created_by,
                     updated_by,
                     deleted,
                     deleted_at,
                     name)
VALUES (nextval('company_seq'), -- 시퀀스를 통해 ID 자동 생성
        now(), -- 생성 일시
        now(), -- 수정 일시
        'system', -- 생성자
        'system', -- 수정자
        false, -- 삭제 여부
        NULL, -- 삭제 일시
        '라인공영' -- 회사명
       );