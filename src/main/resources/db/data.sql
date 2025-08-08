INSERT INTO user_tb (email, password, username, level, role, score)
VALUES
    ('admin1234@nate.com', '1234', '관리자', 'EXPERT', 'ADMIN', 95),
    ('ssar1234@nate.com', '1234', 'ssar', 'BEGINNER', 'USER', 120),
    ('cos1234@nate.com', '1234', 'cos', 'BEGINNER', 'USER', 65),
    ('love1234@nate.com', '1234', 'love', 'INTERMEDIATE', 'USER', 28),
    ('haha1234@nate.com', '1234', 'haha', 'INTERMEDIATE', 'USER', 45);


INSERT INTO provider_tb (name) VALUES ('GOOGLE'), ('NAVER'), ('KAKAO');

INSERT INTO workspace_tb (user_id, title, serialized_json, block_extension_json, created_at)
VALUES
    ( 1, '짜바 연습', null, null, now()),
    ( 2, '짜바 연습', null, null, now()),
    ( 3, '짜바 연습', null, null, now()),
    ( 4, '짜바 연습', null, null, now()),
    ( 5, '짜바 연습', null, null, now());