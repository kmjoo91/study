# Spring Boot Web Application

Java 21과 Gradle을 사용하는 Spring Boot 3.5.3 기반의 웹 애플리케이션입니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.3
- Gradle 8.7
- JUnit 5
- Spring Web
- Spring Data JPA
- H2 Database (In-Memory)
- SLF4J + Log4j2

## 프로젝트 구조

```
src/
├── main/
│   ├── java/me/kmj/
│   │   ├── Application.java
│   │   ├── controller/
│   │   │   ├── HelloController.java
│   │   │   └── MemberController.java
│   │   ├── service/
│   │   │   └── MemberService.java
│   │   ├── repository/
│   │   │   └── MemberRepository.java
│   │   ├── entity/
│   │   │   └── Member.java
│   │   └── config/
│   │       └── DataInitializer.java
│   └── resources/
│       ├── application.yml
│       └── log4j2.xml
└── test/
    └── java/me/kmj/
        ├── ApplicationTests.java
        └── controller/
            ├── HelloControllerTest.java
            └── MemberControllerTest.java
```

## 실행 방법

### 1. 애플리케이션 실행

```bash
# Gradle Wrapper를 사용하여 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/spring-boot-web-0.0.1-SNAPSHOT.jar
```



### 2. 테스트 실행

```bash
./gradlew test
```

## API 엔드포인트

### 기본 API
- `GET /` - 기본 인사말 메시지
- `GET /health` - 헬스 체크

### Member API
- `GET /api/members` - 모든 멤버 조회
- `GET /api/members/{id}` - 특정 멤버 조회
- `POST /api/members` - 새 멤버 생성
- `PUT /api/members/{id}` - 멤버 정보 수정
- `DELETE /api/members/{id}` - 멤버 삭제

### H2 Console
- `GET /h2-console` - H2 데이터베이스 콘솔 (개발용)

## 개발 환경 설정

1. Java 21 설치
2. IDE에서 프로젝트 열기 (IntelliJ IDEA, Eclipse, VS Code, Cursor 등)
3. Gradle 프로젝트로 import

## 빌드 및 배포

```bash
# JAR 파일 생성
./gradlew build

# 실행 가능한 JAR 파일 위치
build/libs/spring-boot-web-0.0.1-SNAPSHOT.jar
``` 