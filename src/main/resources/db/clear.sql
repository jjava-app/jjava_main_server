-- 테이블 초기화용

SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE solved_question_tb;
TRUNCATE TABLE workspace_tb;
TRUNCATE TABLE block_library_tb;
TRUNCATE TABLE question_tb;
TRUNCATE TABLE user_tb;
-- FK 있으면 여기에 관련 테이블도 추가로 TRUNCATE
SET REFERENTIAL_INTEGRITY TRUE;