# agent_assignment

에이전트 활용하여 수강신청 시스템 API 서버를 개발한 과제 리포지토리이다.<br>
001-course-registration 브랜치에서는 180분 이내에 핵심 요구사항 및 문서화를 구현했고, 그 이후로 자체적으로 시간이 날 때마다 무엇을 나아지게 할 수 있을지 고민하며 점진적으로 결과물을 개선했다.

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| Language | Kotlin | 1.9.25 |
| Runtime | Java | 21 |
| Framework | Spring Boot | 3.5.10 |
| Database | MySQL | 8.0 |
| Cache/Lock | Redis | 7.0 |
| API Docs | SpringDoc OpenAPI | - |
| Test Infra Configuration | TestContainers | 1.21.4 |

## 프로젝트 구조

도메인 기반 패키지 구조를 사용합니다.

```
src/main/kotlin/org/example/msstest/
├── common/       # 공통 모듈 (설정, DTO, 예외, 락, 대기열)
├── course/       # 강좌 도메인
├── enrollment/   # 수강신청 도메인
├── student/      # 학생 도메인
└── professor/    # 교수 도메인
```

각 도메인은 `controller/`, `service/`, `repository/`, `entity/`, `dto/` 계층으로 구성됩니다.

## 동시성 제어

수강신청/취소 시 동시 요청에 대한 데이터 정합성을 다층 락 전략으로 보장합니다.

### 정합성이 요구되는 API

| API | 엔드포인트 | 주요 위험 |
|-----|-----------|----------|
| 수강신청 | `POST /api/v1/enrollments` | 정원 초과, 학점 초과, 중복 신청 |
| 수강취소 | `DELETE /api/v1/enrollments` | 정원 카운트 불일치 |

### 적용 기술

| 계층 | 방식 | 적용 대상 | 역할 |
|------|------|----------|------|
| 분산 락 | Redisson (`enrollment:lock:student:{studentId}`) | 수강신청, 수강취소 | 동일 학생의 동시 요청 직렬화 |
| DB 락 | 비관적 락 (`PESSIMISTIC_WRITE`) | 수강신청, 수강취소 | 강좌 정원 변경 시 동시 접근 차단 |
| 낙관적 락 | JPA `@Version` | 수강신청, 수강취소 | 동시 업데이트 충돌 최종 감지 |

### 수강신청 처리 흐름

1. **사전 검증** (락 밖) — 학생/강좌 존재, 중복 신청 확인
2. **분산 락 획득** — Redisson 학생별 락 (wait 5s, lease 10s)
3. **트랜잭션 내 처리** (락 안)
   - 중복 신청 재확인 (이중 검증)
   - 비관적 락으로 강좌 조회
   - 학점 상한 검증 (18학점), 시간표 충돌 검증, 정원 확인
   - 수강 처리 및 정원 증가

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
| GET | `/api/v1/courses` | 강좌 목록 조회 (커서 페이지네이션, 이수구분/학과 필터) |
| GET | `/api/v1/courses/available` | 수강 가능 강좌 조회 |
| GET | `/api/v1/courses/{courseId}` | 강좌 상세 조회 |
| GET | `/api/v1/courses?professorId=` | 교수별 강좌 조회 |
| GET | `/api/v1/students` | 학생 목록 조회 |
| GET | `/api/v1/professors` | 교수 목록 조회 |
| GET | `/api/v1/timetable/{studentId}` | 학생 시간표 조회 |
| POST | `/api/v1/enrollments` | 수강신청 |
| DELETE | `/api/v1/enrollments` | 수강취소 |

### 예시
```bash
# 강좌 목록 조회
curl http://localhost:8080/api/v1/courses

# 커서 기반 페이지네이션 + 이수구분 필터
curl "http://localhost:8080/api/v1/courses?courseType=MAJOR_REQUIRED&size=10"
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

> 통합 테스트는 TestContainers를 사용하므로 Docker가 실행 중이어야 합니다.

## 참고

- 기본 프로파일은 `local`이며 `.env`의 `SPRING_PROFILES_ACTIVE`로 변경할 수 있습니다.
- 데이터베이스/레디스 연결 정보는 `.env`를 사용합니다.
- 상세 설계 문서는 `docs/` 디렉토리를 참고하세요.
