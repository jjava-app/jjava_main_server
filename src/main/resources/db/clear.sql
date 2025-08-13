-- H2: FK 임시 비활성화
SET REFERENTIAL_INTEGRITY FALSE;

-- 1) 자식(의존) 테이블 먼저 비우기
TRUNCATE TABLE solved_question_tb;
TRUNCATE TABLE user_account_provider_tb;  -- 있다면
TRUNCATE TABLE workspace_tb;
TRUNCATE TABLE block_library_tb;

-- 2) 부모 테이블 비우기
TRUNCATE TABLE question_tb;
TRUNCATE TABLE provider_tb;
TRUNCATE TABLE user_tb;

-- 3) (선택) IDENTITY 리셋: ID 컬럼이 IDENTITY인 테이블만 수행
-- 없으면 생략하세요. (복합키 테이블은 수행 X)
ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1;
ALTER TABLE question_tb ALTER COLUMN id RESTART WITH 1;
ALTER TABLE provider_tb ALTER COLUMN id RESTART WITH 1;
ALTER TABLE workspace_tb ALTER COLUMN id RESTART WITH 1;
ALTER TABLE block_library_tb ALTER COLUMN id RESTART WITH 1;
-- ALTER TABLE solved_question_tb ALTER COLUMN id RESTART WITH 1;        -- id 컬럼이 있으면만
-- ALTER TABLE user_account_provider_tb ALTER COLUMN id RESTART WITH 1;  -- id 컬럼이 있으면만

-- FK 재활성화
SET REFERENTIAL_INTEGRITY TRUE;
