# mss_test

수강신청 시스템 API 서버.

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| Language | Kotlin | 1.9.25 |
| Runtime | Java | 21 |
| Framework | Spring Boot | 3.5.10 |
| Database | MySQL | 8.0 |
| Cache/Lock | Redis | 7.0 |
| API Docs | SpringDoc OpenAPI | - |

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

## 초기 데이터 자동 생성

`local`, `dev` 프로파일에서 서버 시작 시 테스트용 초기 데이터가 자동 생성됩니다.

| 항목 | 수량 |
|------|------|
| 학과 | 12개 |
| 교수 | 100명 |
| 강좌 | 500개 |
| 학생 | 10,000명 |

설정은 `application-local.yml`에서 변경 가능합니다:
```yaml
app:
  initializer:
    enabled: true  # false로 변경 시 비활성화
    professors: 100
    courses: 500
    students: 10000
```

## API 서버 접속 정보

- 기본 포트: `8080`
- 기본 Base URL: `http://localhost:8080`
- API Prefix: `/api/v1`
- Health Check: `GET /health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 주요 API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/v1/courses` | 강좌 목록 조회 |
| GET | `/api/v1/students` | 학생 목록 조회 |
| GET | `/api/v1/professors` | 교수 목록 조회 |
| GET | `/api/v1/timetable/{studentId}` | 학생 시간표 조회 |
| POST | `/api/v1/enrollments` | 수강신청 |
| DELETE | `/api/v1/enrollments` | 수강취소 |

### 예시
```bash
curl http://localhost:8080/api/v1/courses
```

## 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 단위 테스트만
./gradlew test --tests "*Test"

# 통합 테스트만
./gradlew test --tests "*IntegrationTest"
```

## 참고

- 기본 프로파일은 `local`이며 `.env`의 `SPRING_PROFILES_ACTIVE`로 변경할 수 있습니다.
- 데이터베이스/레디스 연결 정보는 `.env`를 사용합니다.
- 상세 설계 문서는 `docs/` 디렉토리를 참고하세요.
