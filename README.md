# 심화프로젝트 - OpenAI를 이용한 Springboot 기반 앱개발 : 짜바

- 자바와 스프링부트를 활용하여 Rest API 서버를 제작하였습니다.
- 전체 개발 기간 : 2025.06.16 ~ 2025.07.18
  <br />

## Ideas
***"앱으로 블록코딩을 만들 수 없을까?"***
<br>
***"알고리즘 말고 코드를 이해하게 만들 수 없을까?"***

## Referenced

- Scratch, Entry, Blockly를 참고하여 제작하였습니다.

<a href="https://scratch.mit.edu/">
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkB_9ED3-s4QuRdkF3USqn1nzLQ0YP3q_g2A&s" width="150">
</a>
<a href="https://playentry.org/">
<img src="https://cdn.imweb.me/thumbnail/20220413/1feae8497c570.png" width="150">
</a>
<a href="https://developers.google.com/blockly?hl=ko">
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSDBtSh8-za2e1sA62auEPXOZ7OfA1LnM-nDw&s" width="150">
</a>

# 👥 팀 멤버

| 이름  | 역할  | GitHub                                       |
|-----|-----|----------------------------------------------|
| 문정준 | 팀장  | [@Sxias](https://github.com/Sxias)           |
| 손영민 | 팀원 | [@son7571](https://github.com/son7571)       |
| 김미숙 | 팀원  | [@parangdajavous](https://github.com/parangdajavous) |
| 이민경 | 팀원  | [@alsrud-602](https://github.com/alsrud-602)       |

<br />

# ⚙️ 기술 스택

## 🛠️ 사용 기술

<table>
    <tr>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="50"/><br/>Java</td>
        <td align="center"><img src="https://cdn.simpleicons.org/springboot/6DB33F" width="50"/><br/>Spring Boot</td>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="50"/><br/>RestDoc</td>
        <td align="center"><img src="https://cdn.simpleicons.org/hibernate/59666C" width="50"/><br/>JPA(Hibernate)</td>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/sqlite/sqlite-original.svg" width="50"/><br/>H2</td>
        <td align="center"><img src="https://cdn.simpleicons.org/mysql/4479A1" width="50"/><br/>MySQL</td>
    </tr>
</table>
<table>
    <tr>
        <td align="center"><img src="https://cdn.simpleicons.org/firebase/FFCA28" width="50"/><br/>FCM</td>
        <td align="center"><img src="https://cdn.simpleicons.org/kakao/FEE500" width="50"/><br/>Kakao OIDC</td>
        <td align="center"><img src="https://cdn.simpleicons.org/githubactions/2088FF" width="50"/><br/>Github Action</td>
        <td align="center"><img src="https://img.icons8.com/?size=256&id=33039&format=png" width="50"/><br/>aws</td>
        <td align="center"><img src="https://cdn.simpleicons.org/sentry/362D59" width="50"/><br/>Sentry</td>
    </tr>
</table>

## 🧰 개발 환경

<table>
    <tr>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/intellij/intellij-original.svg" width="50"/><br/>IntelliJ</td>
    </tr>
</table>

## 🤝 협업 도구

<table>
    <tr>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/git/git-original.svg" width="50"/><br/>Git</td>
        <td align="center"><img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="50"/><br/>GitHub</td>
        <td align="center"><img src="https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png" width="50"/><br/>Notion</td>
        <td align="center"><img src="https://upload.wikimedia.org/wikipedia/commons/7/76/Slack_Icon.png" width="50"/><br/>Slack</td>
    </tr>
</table>

<br>

# 📋 프로젝트 업무 분담

<table style="width: 100%; text-align: start; font-size: 16px; border-collapse: collapse;">
    <thead style="background-color: #f2f2f2;">
        <tr>
            <th style="padding: 10px; border: 1px solid #ddd;">담당자</th>
            <th style="padding: 10px; border: 1px solid #ddd;">프로젝트 업무 분담</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="padding: 10px; border: 1px solid #ddd;">문정준</td>
            <td style="padding: 10px; border: 1px solid #ddd;">
                <ul>
                    <li>프로젝트 계획 및 관리</li>
                    <li>팀 리딩 및 커뮤니케이션</li>
          <li>워크스페이스 CRUD 구현</li>
<li>Redis 원격 세션 구현</li>
<li>Springboot Security 보안 처리</li>
                </ul>
            </td>
        </tr>
        <tr>
            <td style="padding: 10px; border: 1px solid #ddd;">손영민</td>
            <td style="padding: 10px; border: 1px solid #ddd;">
                <ul>
                    <li>OAuth 2.0 인증 기능 구현</li>
                    <li>내 정보 열람 및 수정 기능 구현</li>
                    <li>OAuth 계정 연동 서비스 구현</li>
                    <li>관리자용 문제 CRUD 구현</li>    
                </ul>
            </td>
        </tr>
        <tr>
            <td style="padding: 10px; border: 1px solid #ddd;">김미숙</td>
            <td style="padding: 10px; border: 1px solid #ddd;">
                <ul>
                    <li>코드 원격 컴파일 기능 개발</li>
                    <li>코드 체크 및 AI 첨삭 기능 개발</li>
                    <li>이메일 인증 기능 개발</li>
                    <li>컴파일용 서버 제작</li>
                    <li>docker-compose 코드 구현</li>
                </ul>
            </td>
        </tr>
        <tr>
            <td style="padding: 10px; border: 1px solid #ddd;">이민경</td>
            <td style="padding: 10px; border: 1px solid #ddd;">
                <ul>
                    <li>회원가입 및 로그인 기능 개발</li>
                    <li>문제 리스트 및 상세정보 출력 기능 개발</li>
                    <li>관리자용 유저 CRUD 구현</li>
                </ul>
            </td>
        </tr>
    </tbody>
</table>

# 주요 기능

### 로그인/회원가입

- 앱 내 로그인, 회원가입
- 구글/카카오/네이버 로그인
- 유효성 검사
- 이메일 인증

### 워크스페이스

- 목록 : 내가 만든 워크스페이스 목록 보기
- 생성 : 새 워크스페이스 생성
- 저장 : 현재 워크스페이스 상태 저장
- 삭제 : 워크스페이스 삭제
- 컴파일 : 워크스페이스 내 블록 코드 컴파일 후 실행 결과 확인

### 문제
- 목록 : 내가 풀지 않은 / 진행 중인 문제 목록 보기
- 검사 : 블록 코딩으로 구현한 코드를 테스트 더미와 함께 검산 후 AI 첨삭과 함께 결과 출력

### 지난 문제
- 목록 : 내가 푼 문제 목록 보기
- 상세 보기 : 내가 푼 문제의 제목, 내용, AI 첨삭 확인 가능

### 마이페이지
- 내 정보 : 내 정보 및 계정 연동 현황 확인 가능
- 수정 : 내 닉네임, 학습 레벨 수정 가능


# 테이블

### ▶️ user_tb

```sql
CREATE TABLE user_tb (
  id INT NOT NULL AUTO_INCREMENT,
  score INT,
  email VARCHAR(255),
  password VARCHAR(255),
  username VARCHAR(255),
  level ENUM('BEGINNER','EXPERT','INTERMEDIATE'),
  role  ENUM('ADMIN','USER'),
  PRIMARY KEY (id)
)
```

### ▶️ provider_tb

```sql
CREATE TABLE provider_tb (
  id INT NOT NULL AUTO_INCREMENT,
  provider_type ENUM('GOOGLE','KAKAO','NAVER'),
  PRIMARY KEY (id)
)
```

### ▶️ question_tb

```sql
CREATE TABLE question_tb (
  id INT NOT NULL AUTO_INCREMENT,
  content VARCHAR(255),
  test_answer VARCHAR(255),
  test_variable VARCHAR(255),
  title VARCHAR(255),
  type ENUM('ARRAY','CONDITIONAL','LOOP','OPERATOR','TEXT'),
  PRIMARY KEY (id)
)
```

### ▶️ block_library_tb

```sql
CREATE TABLE block_library_tb (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT,
  library_json VARCHAR(255),
  PRIMARY KEY (id)
)
```

### ▶️ rank_tb

```sql
CREATE TABLE rank_tb (
  after_score INT,
  before_score INT,
  delta_score INT,
  id INT NOT NULL AUTO_INCREMENT,
  rank_number INT,
  user_id INT,
  PRIMARY KEY (id)
)
```

### ▶️ scoreboard_tb

```sql
CREATE TABLE scoreboard_tb (
  base_score INT,
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT UNIQUE,
  updated_at TIMESTAMP(6) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_scoreboard_user_id (user_id)
)
```

### ▶️ solved_question_tb

```sql
CREATE TABLE solved_question_tb (
  id INT NOT NULL AUTO_INCREMENT,
  question_id INT,
  user_id INT,
  created_at TIMESTAMP(6) NULL,
  ai_comment VARCHAR(255),
  block_extension_json VARCHAR(255),
  serialized_json VARCHAR(255),
  progress_status ENUM('IN_PROGRESS','REVIEWED'),
  PRIMARY KEY (id)
)
```

### ▶️ user_account_provider_tb

```sql
CREATE TABLE user_account_provider_tb (
  id INT NOT NULL,
  provider_id INT,
  user_id INT,
  provider_user_id VARCHAR(255),
  PRIMARY KEY (id)
)
```

### ▶️ workspace_tb

```sql
CREATE TABLE workspace_tb (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT,
  created_at TIMESTAMP(6) NULL,
  block_extension_json VARCHAR(255),
  serialized_json VARCHAR(255),
  title VARCHAR(255),
  PRIMARY KEY (id)
)
```
