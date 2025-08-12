INSERT INTO user_tb (email, password, username, level, role, score)
VALUES ('admin1234@nate.com', '1234', '관리자', 'EXPERT', 'ADMIN', 95),
       ('ssar1234@nate.com', '1234', 'ssar', 'BEGINNER', 'USER', 120),
       ('cos1234@nate.com', '1234', 'cos', 'BEGINNER', 'USER', 65),
       ('love1234@nate.com', '1234', 'love', 'INTERMEDIATE', 'USER', 28),
       ('haha1234@nate.com', '$2a$10$AIzYzbpY0rscDg2IWk/3A.Tzh74h5zGyq0cHtlnLJqfjIJy6/iOZ6', 'haha', 'INTERMEDIATE',
        'USER', 45);


INSERT INTO provider_tb (name)
VALUES ('GOOGLE'),
       ('NAVER'),
       ('KAKAO');

INSERT INTO workspace_tb (user_id, title, serialized_json, block_extension_json, created_at)
VALUES (1, '짜바 연습', null, null, now()),
       (2, '짜바 연습', null, null, now()),
       (3, '짜바 연습', null, null, now()),
       (4, '짜바 연습', null, null, now()),
       (5, '짜바 연습', null, null, now());

INSERT INTO block_library_tb (user_id, library_json)
VALUES (1, null),
       (2, null),
       (3, null),
       (4, null),
       (5, null);


-- question_tb
INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('OPERATOR',
        '두 수의 합 구하기',
        '문제 설명: 정수 a, b가 주어질 때, 두 수의 합을 반환하는 함수를 완성하세요.\\n\\n제한사항: a와 b는 -1000 이상 1000 이하의 정수입니다.\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- int: a + b 의 결과를 반환합니다.',
        '[{"a":2,"b":3},{"a":10,"b":15},{"a":-1,"b":5}]',
        '[5,25,4]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('OPERATOR',
        '두 수의 차 구하기',
        '문제 설명: 정수 a, b가 주어질 때, a - b 의 결과를 반환하는 함수를 완성하세요.\\n\\n제한사항: a와 b는 -1000 이상 1000 이하의 정수입니다.\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- int: a - b 의 결과를 반환합니다.',
        '[{"a":5,"b":3},{"a":10,"b":20},{"a":20,"b":5}]',
        '[2,-10,15]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('OPERATOR',
        '두 수의 곱 구하기',
        '문제 설명: 정수 a, b가 주어질 때, 두 수의 곱을 반환하는 함수를 완성하세요.\\n\\n제한사항: a와 b는 -1000 이상 1000 이하의 정수입니다.\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- int: a * b 의 결과를 반환합니다.',
        '[{"a":2,"b":3},{"a":-2,"b":5},{"a":-4,"b":-3}]',
        '[6,-10,12]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('OPERATOR',
        '두 수의 나눗셈 (실수 반환)',
        '문제 설명: 정수 a, b가 주어질 때, a를 b로 나눈 실수값을 반환하는 함수를 완성하세요.\\n\\n단, 소수점 둘째 자리까지 출력되도록 하세요.\\n\\n제한사항:\\n- a는 -10000 이상 10000 이하의 정수\\n- b는 0이 아닌 정수\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- float: a / b 의 결과 (소수점 둘째 자리까지)',
        '[{"a":7,"b":2},{"a":10,"b":4},{"a":-9,"b":2}]',
        '[3.5,2.5,-4.5]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('OPERATOR',
        '두 수의 몫 구하기',
        '문제 설명: 정수 a, b가 주어질 때, a를 b로 나눈 몫(정수)을 반환하는 함수를 완성하세요.\\n\\n나눗셈은 내림이 아닌 정수 나눗셈으로 처리합니다.\\n\\n제한사항:\\n- a는 -10000 이상 10000 이하의 정수\\n- b는 0이 아닌 정수\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- int: a를 b로 나눈 몫',
        '[{"a":10,"b":2},{"a":5,"b":2},{"a":-7,"b":2}]',
        '[3,2,-3]');


INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('TEXT',
        '문자열 붙이기',
        '문제 설명: 문자열 a와 b가 주어질 때, 두 문자열을 붙여서 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- string a\\n- string b\\n\\n출력 형식:\\n- string: a와 b를 순서대로 붙인 결과',
        '[{"a":"Hello","b":"World"},{"a":"I love","b":"you"},{"a":"A","b":"B"}]',
        '["HelloWorld","I loveyou","AB"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('TEXT',
        '문자열 길이 구하기',
        '문제 설명: 문자열 a가 주어질 때, 이 문자열의 길이를 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- string a\\n\\n출력 형식:\\n- int: 문자열 a의 길이',
        '[{"a":"hello"},{"a":"abcdefg"},{"a":"hi"}]',
        '[5,7,2]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('TEXT',
        '문자열이 비었는지 확인하기',
        '문제 설명: 문자열 a가 주어질 때, 문자열이 비어있으면 true, 아니면 false를 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- string a\\n\\n출력 형식:\\n- boolean: 빈 문자열 여부',
        '[{"a":""},{"a":"abc"},{"a":" "}]',
        '[true,false,true]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('TEXT',
        '특정 글자의 위치 찾기',
        '문제 설명: 문자열 a와 문자 b가 주어질 때, b가 a에서 처음 등장하는 위치(0부터 시작)를 반환하세요. 존재하지 않으면 -1을 반환하세요.\\n\\n입력 형식:\\n- string a\\n- string b (길이 1)\\n\\n출력 형식:\\n- int: 첫 등장 위치 또는 -1',
        '[{"a":"apple","b":"p"},{"a":"hello","b":"l"},{"a":"world","b":"z"}]',
        '[1,2,-1]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('TEXT',
        '문자열 대문자로 바꾸기',
        '문제 설명: 문자열 a가 주어질 때, 모든 문자를 대문자로 바꿔서 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- string a\\n\\n출력 형식:\\n- string: 대문자로 변환된 문자열',
        '[{"a":"hello"},{"a":"GoodBye"},{"a":"java"}]',
        '["HELLO","GOODBYE","JAVA"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('LOOP',
        'Hello를 5번 출력하세요.',
        '문제 설명: "Hello"라는 문장을 5번 출력하도록 블록을 완성하세요.\\n\\n입력 형식:\\n- 없음\\n\\n출력 형식:\\n- string: Hello가 줄바꿈 없이 5번 이어진 문자열을 반환합니다.',
        '[{"a":"Hello"},{"a":"string"},{"a":"int"}]',
        '["HelloHelloHelloHelloHello","stringstringstringstringstring","intintintintint"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('LOOP',
        '1부터 n까지의 합 구하기',
        '문제 설명: 정수 n이 주어질 때, 1부터 n까지의 합을 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- int n\\n\\n출력 형식:\\n- int: 1 + 2 + ... + n 의 결과',
        '[{"n":3},{"n":5},{"n":10}]',
        '[6,15,55]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('LOOP',
        '문자열을 n번 반복하기',
        '문제 설명: 문자열 a와 정수 n이 주어질 때, 문자열을 n번 반복한 결과를 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- string a\\n- int n\\n\\n출력 형식:\\n- string: 문자열이 n번 반복된 결과',
        '[{"a":"hi","n":3},{"a":"yo","n":2},{"a":"ok","n":4}]',
        '["hihihi","yoyo","okokokok"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('LOOP',
        '0부터 n까지 짝수만 더하기',
        '문제 설명: 정수 n이 주어질 때, 0부터 n까지 짝수만 더한 값을 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- int n\\n\\n출력 형식:\\n- int: 0, 2, 4, ..., n 중 짝수들의 합',
        '[{"n":6},{"n":10},{"n":3}]',
        '[12,30,2]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('CONDITIONAL',
        '짝수인지 확인하기',
        '문제 설명: 정수 a가 주어질 때, a가 짝수이면 true, 아니면 false를 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- int a\\n\\n출력 형식:\\n- boolean: 짝수 여부',
        '[{"a":4},{"a":7},{"a":0}]',
        '[true,false,true]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('CONDITIONAL',
        '큰 수 반환하기',
        '문제 설명: 정수 a, b가 주어질 때, 두 수 중 더 큰 수를 반환하는 함수를 완성하세요.\\n\\n입력 형식:\\n- int a\\n- int b\\n\\n출력 형식:\\n- int: 더 큰 정수',
        '[{"a":3,"b":5},{"a":10,"b":7},{"a":4,"b":4}]',
        '[5,10,4]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('CONDITIONAL',
        '성인인지 판별하기',
        '문제 설명: 나이를 나타내는 정수 age가 주어질 때, 20살 이상이면 "성인", 그렇지 않으면 "미성년자"를 반환하세요.\\n\\n입력 형식:\\n- int age\\n\\n출력 형식:\\n- string: "성인" 또는 "미성년자"',
        '[{"age":25},{"age":19},{"age":20}]',
        '["성인","미성년자","성인"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('CONDITIONAL',
        '점수 등급 출력하기',
        '문제 설명: 정수 score가 주어질 때, 다음 조건에 따라 등급을 반환하세요:\\n- 90점 이상: "A"\\n- 80점 이상 90점 미만: "B"\\n- 나머지: "C"\\n\\n입력 형식:\\n- int score\\n\\n출력 형식:\\n- string: 등급 ("A", "B", "C")',
        '[{"score":95},{"score":85},{"score":70}]',
        '["A","B","C"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('ARRAY',
        '첫 번째 요소 구하기',
        '문제 설명: 문자열 리스트 arr가 주어질 때, 첫 번째 요소를 반환하세요.\\n\\n입력 형식:\\n- list arr\\n\\n출력 형식:\\n- string: 첫 번째 요소',
        '[{"arr":["apple","banana"]},{"arr":["dog","cat","bird"]},{"arr":["A","B","C"]}]',
        '["apple","dog","A"]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('ARRAY',
        '리스트의 길이 구하기',
        '문제 설명: 정수 리스트 arr가 주어질 때, 리스트의 길이를 반환하세요.\\n\\n입력 형식:\\n- list arr\\n\\n출력 형식:\\n- int: 요소 개수',
        '[{"arr":[1,2,3]},{"arr":[7]},{"arr":[4,5,6,7]}]',
        '[3,1,4]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('ARRAY',
        '리스트의 합 구하기',
        '문제 설명: 정수 리스트 arr가 주어질 때, 모든 요소의 합을 반환하세요.\\n\\n입력 형식:\\n- list arr\\n\\n출력 형식:\\n- int: 리스트 요소의 총합',
        '[{"arr":[1,2,3]},{"arr":[10,20,30]},{"arr":[5]}]',
        '[6,60,5]');

INSERT INTO question_tb (type, title, content, test_variable, test_answer)
VALUES ('ARRAY',
        '가장 큰 수 구하기',
        '문제 설명: 정수 리스트 arr가 주어질 때, 가장 큰 수를 찾아 반환하세요.\\n\\n입력 형식:\\n- list arr\\n\\n출력 형식:\\n- int: 가장 큰 수',
        '[{"arr":[3,6,2]},{"arr":[10,5,8,1]},{"arr":[7]}]',
        '[6,10,7]');


-- solved_question_tb
INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (2, 5, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (2, 6, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (2, 7, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (3, 8, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (3, 1, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (3, 2, '리팩토링', 'json', 'json');

INSERT INTO solved_question_tb (user_id, question_id, ai_comment, serialized_json, block_extension_json)
VALUES (3, 3, '리팩토링', 'json', 'json');

