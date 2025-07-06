# 모임 관리 시스템 (Meeting Management System)

Java 21과 Gradle을 사용하는 Spring Boot 3.5.3 기반의 모임 관리 시스템입니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.3
- Gradle 8.7
- JUnit 5
- Spring Web
- Spring Data JPA
- H2 Database (In-Memory)
- SLF4J + Log4j2
- Spring Validation

## 프로젝트 구조

```
src/
├── main/
│   ├── java/me/kmj/gather/
│   │   ├── Application.java
│   │   ├── controller/
│   │   │   ├── MemberController.java
│   │   │   ├── GroupInfoController.java
│   │   │   ├── GroupMemberController.java
│   │   │   ├── AdminController.java
│   │   │   └── HealthCheckController.java
│   │   ├── service/
│   │   │   ├── MemberService.java
│   │   │   ├── GroupInfoService.java
│   │   │   └── GroupMemberService.java
│   │   ├── repository/
│   │   │   ├── MemberRepository.java
│   │   │   ├── GroupInfoRepository.java
│   │   │   └── GroupMemberRepository.java
│   │   ├── entity/
│   │   │   ├── Member.java
│   │   │   ├── GroupInfo.java
│   │   │   └── GroupMember.java
│   │   ├── dto/
│   │   │   ├── MemberDto.java
│   │   │   ├── GroupInfoDto.java
│   │   │   └── GroupMemberDto.java
│   │   └── config/
│   │       └── DataInitializer.java
│   └── resources/
│       ├── application.yml
│       └── log4j2.xml
└── test/
    └── java/me/kmj/gather/
        └── ApplicationTests.java
```

## 실행 방법

### 1. 애플리케이션 실행

```bash
# Gradle Wrapper를 사용하여 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/meeting-app-0.0.1-SNAPSHOT.jar
```

**참고**: 애플리케이션 실행 시 자동으로 다음 작업이 수행됩니다:
- H2 메모리 데이터베이스 초기화
- `member`, `group_info`, `group_member` 테이블 자동 생성
- 샘플 데이터 자동 삽입 (user001~user005, 그룹, 가입 신청 데이터)
- 한글 로그 지원 (UTF-8 인코딩)

### 2. 테스트 실행

```bash
./gradlew test
```

## API 엔드포인트

### 헬스체크 API
- `GET /health` - 애플리케이션 상태 확인

### 회원 관리 API
- `POST /api/members/register` - 회원가입
- `GET /api/members` - 전체 회원 목록 조회
- `GET /api/members/{id}` - 회원 조회 (ID로)
- `PUT /api/members/{id}` - 회원 정보 수정
- `DELETE /api/members/{id}` - 회원 삭제



### 관리자 API
- `GET /api/admin/statistics` - 전체 통계 조회
- `GET /api/admin/groups/{groupId}/statistics` - 그룹별 상세 통계
- `GET /api/admin/groups` - 전체 그룹 목록 조회 (관리자용)
- `GET /api/admin/members` - 전체 회원 목록 조회 (관리자용)

### 그룹 관리 API
- `POST /api/groups?createdById={id}` - 그룹 생성
- `GET /api/groups` - 페이징 처리된 그룹 목록 조회
- `GET /api/groups/{id}` - 그룹 상세 조회
- `PUT /api/groups/{id}?createdById={id}` - 그룹 수정
- `DELETE /api/groups/{id}?createdById={id}` - 그룹 삭제
- `GET /api/groups/search?keyword={keyword}` - 그룹명으로 검색
- `GET /api/groups/creator/{createdById}` - 특정 사용자가 생성한 그룹 목록

### 그룹 멤버 관리 API
- `POST /api/group-members/request?groupId={id}&memberId={id}` - 그룹 가입 신청
- `PUT /api/group-members/{id}/approve?processorId={id}` - 그룹 가입 승인
- `PUT /api/group-members/{id}/reject?processorId={id}` - 그룹 가입 거절
- `GET /api/group-members/group/{groupId}` - 특정 그룹의 멤버 목록 조회
- `GET /api/group-members/member/{memberId}` - 특정 회원의 그룹 가입 신청 목록 조회
- `GET /api/group-members/group/{groupId}/approved-count` - 승인된 멤버 수
- `GET /api/group-members/group/{groupId}/pending-count` - 대기중인 가입 신청 수

### H2 Console
- `GET /h2-console` - H2 데이터베이스 콘솔 (개발용)

## 주요 기능

### 🔹 시스템 기능
- **헬스체크**: 애플리케이션 상태 확인
- **한글 로그**: UTF-8 인코딩으로 한글 로그 지원

### 🔹 회원 기능
- **회원가입**: 이름, 성별, 전화번호, 주소 입력
- **회원조회**: ID로 단일 조회, 전체 목록 조회



### 🔹 그룹 기능
- **그룹 생성**: 그룹명, 설명 입력 (생성자 자동 매핑)
- **그룹 수정/삭제**: 생성자 본인만 가능
- **그룹 목록 조회**: 페이징 처리된 전체 리스트
- **그룹 상세 조회**: 그룹 + 멤버 정보 포함

### 🔹 그룹 멤버 기능
- **가입 신청**: 로그인 회원이 그룹에 가입 신청
- **승인/거절**: 그룹 생성자가 처리
- **내 그룹 목록 조회**: 본인이 가입한 그룹 내역 확인

### 🔹 관리자 기능
- **전체 그룹 및 멤버 현황 확인**

## 개발 환경 설정

1. Java 21 설치
2. IDE에서 프로젝트 열기 (IntelliJ IDEA, Eclipse, VS Code, Cursor 등)
3. Gradle 프로젝트로 import

## 빌드 및 배포

```bash
# JAR 파일 생성
./gradlew build

# 실행 가능한 JAR 파일 위치
build/libs/meeting-app-0.0.1-SNAPSHOT.jar
```

## 샘플 데이터

애플리케이션 시작 시 자동으로 생성되는 샘플 데이터:

### 회원
- `user001` - 김철수
- `user002` - 이영희
- `user003` - 박민수
- `user004` - 정수진
- `user005` - 최동현

### 그룹
- 개발자 커뮤니티
- 독서 모임
- 운동 모임

### 가입 신청
- 각 그룹에 대한 다양한 상태의 가입 신청 데이터 