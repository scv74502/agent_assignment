# mss_test

수강신청 시스템 API 서버.

## 프로젝트 빌드 방법

```bash
./gradlew build
```

## 서버 실행 방법

1. 인프라 실행 (MySQL, Redis)
```bash
docker-compose up -d
```

2. 환경 변수 설정
```bash
cp .env.example .env
```

3. 애플리케이션 실행
```bash
./gradlew bootRun
# 또는
./gradlew build
java -jar build/libs/mss_test-0.0.1-SNAPSHOT.jar
```

## API 서버 접속 정보 (포트 등)

- 기본 포트: `8080`
- 기본 Base URL: `http://localhost:8080`
- API Prefix: `/api/v1`
- Health Check: `GET /health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 예시
```bash
curl http://localhost:8080/api/v1/courses
```

## 참고

- 기본 프로파일은 `local`이며 `.env`의 `SPRING_PROFILES_ACTIVE`로 변경할 수 있습니다.
- 데이터베이스/레디스 연결 정보는 `.env`를 사용합니다.
