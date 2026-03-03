# AI 활용 코드 개발 워크플로우 상세 기록

> 수강신청 시스템 개발 과정에서의 Claude 활용 내역 (면접 준비 제외)
> 개발일: 2026-02-08, 총 소요시간: 약 2시간 (15:53 ~ 17:57)

---

## 1. 프로젝트 초기화 — 사용자 직접 수행 (15:53~15:56)

### 수행 내용
- Spring Initializr로 프로젝트 생성
- **CLAUDE.md를 사용자가 직접 작성** — AI에게 줄 지침서
- docs/REQUIREMENTS.md 빈 파일 생성

### CLAUDE.md에 사전 정의한 규칙
| 영역 | 규칙 |
|------|------|
| 코틀린 | value object, inline function, sealed class 사용 |
| Spring | Swagger-First (인터페이스 → 구현체), 레이어드 아키텍처 |
| 테스트 | 단위+통합 모두 구성, 통합은 TestContainer 사용 |
| 에러 처리 | global handler, 종류별 handler 분리 |
| 커밋 | `[type] 한글 제목` 형식, Co-Authored-By 제외 |

### AI 활용 의의
- 이 단계에서 AI가 따를 **코딩 컨벤션, 아키텍처 패턴, 커밋 규칙을 사전에 정의**
- 이후 AI가 생성하는 모든 코드의 품질과 일관성을 결정하는 핵심 기반
- **AI를 "제어"하기 위한 harness 설계**에 해당

### 커밋
```
9bbd746 15:56:18 [init] spring boot 프로젝트 초기화 (13파일, +523줄)
```

---

## 2. HealthCheck API — 프롬프트 파일 기반 지시 (15:53)

### 사용자 프롬프트 (`prompts/heathCheck.md`)
```
HealthCheckApi를 먼저 swagger로 구성하고 HealthCheckAPI 구현해.
```
코드 예시(`GET /health`, `@Operation` 어노테이션 형식)까지 프롬프트에 포함.

### AI 생성물
| 파일 | 역할 | 비고 |
|------|------|------|
| `config/SwaggerConfig.kt` | OpenAPI 설정 | 39줄 |
| `controller/openapi/HealthCheckApi.kt` | Swagger 인터페이스 | 21줄 |
| `controller/HealthCheckController.kt` | 구현체 | 10줄 |

### AI 활용 방식
- **프롬프트 파일(.md)에 요구사항+코드 예시를 작성하여 전달**
- CLAUDE.md의 "Swagger-First" 규칙에 따라 인터페이스 → 구현체 순서 생성
- 이 패턴이 이후 모든 API 구현에서 반복됨

### 커밋
```
9c357f5 15:53:32 [feat] Swagger 및 HealthCheckApi 추가 (6파일, +91줄)
```

---

## 3. 요구사항 분석 및 스펙 정리 (16:27)

### 사용자 프롬프트
```
"speckit 활용하여 스펙 주도 개발 설정"
```

### 수행 내용
- 사용자가 `prompts/REQUIRED_SPEC.md`를 직접 작성 (65줄)
  - 과제 문서(`MSS_PROBLEM.MD`)를 분석하여 기능/비기능 요구사항 정리
  - API 명세 초안, 데이터 모델, 기술 스택 정의
  - **설계 고민과 이유** 섹션 포함: 동시성 제어 전략, 시간표 충돌 검증(반개구간), 학점 제한 검증
- CLAUDE.md에 "기록 관리" 규칙 추가 — AI 작업 이력을 history.md에 기록하도록

### AI 활용 의의
- speckit 에이전트 시도했으나 최종 결과물에는 미포함 (`.speckit/` 디렉토리 부재)
- 대신 **사용자가 직접 요구사항 분석서를 작성**하여 AI에게 전달할 컨텍스트를 구축
- AI에게 "무엇을 만들어야 하는지"를 명확히 전달하기 위한 **입력 문서 준비** 단계

### 커밋
```
b8b72ab 16:27:23 [feat] .gitignore 개편, 에이전트 관련 마크다운 추가 (4파일, +80줄)
```

---

## 4. MVP 핵심 구현 — Plan-First 일괄 구현 (16:31~16:49)

> **이 프로젝트에서 가장 핵심적인 AI 활용 구간.**
> 약 18분 만에 전체 핵심 기능이 구현됨.

### 사용자 프롬프트
```
"Implement the following plan: # Implementation Plan: 수강신청 시스템 ..."
```
사용자가 **구현 계획(Implementation Plan)을 미리 작성**하여 Phase 1~5로 나눈 뒤 AI에게 전달.

### Phase별 AI 생성물

#### Phase 1: 프로젝트 설정 및 인프라 (16:48:39)
| 파일 | 내용 |
|------|------|
| `build.gradle.kts` | 의존성 33줄 추가 (JPA, Security, Redis, Redisson, JWT, TestContainers) |
| `docker-compose.yml` | MySQL 8.0 + Redis 7-alpine (39줄, 신규) |
| `application.yml` | local/test 프로파일 분리, DB/Redis 연결, JWT, enrollment 설정 (77줄) |

#### Phase 2: 기반 코드 (16:48:47) — **28파일, +1,100줄 (최대 규모 커밋)**
| 카테고리 | 생성 파일 |
|----------|----------|
| Config | JpaConfig, RedisConfig, SecurityConfig |
| Entity | BaseEntity, Course(75줄), CourseSchedule(57줄), Enrollment(53줄), Professor(30줄), Student(33줄) |
| Value Object | Credits(16줄), StudentNo(14줄) |
| DTO | EnrollmentRequest, CourseResponse(111줄), EnrollmentResponse, ErrorResponse, ProfessorResponse, StudentResponse, TimetableResponse |
| Exception | EnrollmentException(sealed class, 53줄), ErrorCode(enum, 40줄), GlobalExceptionHandler(48줄) |
| Redis | RedisLockService(70줄 — Redisson 분산 락), EnrollmentQueueService(89줄) |
| Repository | CourseRepository(34줄, PESSIMISTIC_WRITE 포함), CourseScheduleRepository(26줄), EnrollmentRepository(54줄), ProfessorRepository, StudentRepository |

#### Phase 3: 수강신청 API (16:48:57)
| 파일 | 내용 |
|------|------|
| `controller/openapi/EnrollmentApi.kt` | Swagger 인터페이스 (95줄) |
| `controller/EnrollmentController.kt` | 구현체 (29줄) |
| `service/EnrollmentService.kt` | 핵심 비즈니스 로직 — 분산 락 획득 → 검증 → 신청/취소 (148줄) |

#### Phase 4-5: 조회 API (16:49:06)
- Course/Student/Professor/Timetable 각각 Api(인터페이스) + Controller + Service
- 12파일, +468줄

#### 테스트 (16:49:15)
| 파일 | 내용 |
|------|------|
| `IntegrationTestBase.kt` | TestContainers 기반 통합 테스트 베이스 (55줄) |
| `TestContainerConfig.kt` | MySQL + Redis 컨테이너 설정 (47줄) |
| `CourseScheduleTest.kt` | 시간 충돌 판정 단위 테스트 (102줄) |
| `CreditsTest.kt` | Value Object 단위 테스트 (62줄) |
| `EnrollmentServiceConcurrencyTest.kt` | **100명 동시 신청 테스트** (166줄) |
| `EnrollmentServiceTest.kt` | 서비스 레이어 통합 테스트 (248줄) |

### AI 활용 방식
- **Plan-First 패턴**: 사용자가 구현 계획 작성 → AI에게 일괄 구현 지시
- 커밋 간 시간 간격이 **8~10초** → AI가 한 번에 전체를 생성한 후, 사용자가 `git add` 범위를 조절하며 **커밋만 수동으로 분리**
- 실제 AI 구현 소요시간은 약 16:31 ~ 16:48 (**약 17분**)
- 총 생성량: **49파일, +2,667줄**

### 커밋
```
7dabb82 16:48:39 [feat] 프로젝트 설정 및 인프라 구성        (4파일, +147줄)
9d5178f 16:48:47 [feat] 기반 코드 구현                      (28파일, +1,100줄)
7b75a94 16:48:57 [feat] 수강신청 API 구현                   (3파일, +272줄)
e722522 16:49:06 [feat] 조회 API 구현                       (12파일, +468줄)
15bb2fc 16:49:15 [test] 단위 테스트 및 통합 테스트 구현      (6파일, +680줄)
```

---

## 5. 리팩토링 — 분석-결정-실행 3단계 (16:48~17:32)

### 5-1. 분석 단계: 병렬 에이전트 투입

#### 사용자 프롬프트
```
"현재 디렉토리 구조가 클린하지 않아 엉망인데 리팩토링 계획을 세워봐"
```

#### AI 작업 방식
**탐색 에이전트 3개를 병렬 실행** (16:49 ~ 16:55):
1. **설정 파일 분석 에이전트** — application.yml, docker-compose.yml, Config 클래스 분석
2. **디렉토리 구조 분석 에이전트** — 계층별 역할, 네이밍 일관성, 패키지 구조 분석
3. **테스트 코드 분석 에이전트** — 커버리지 격차, 설정 중복, 테스트 인프라 분석

각 에이전트가 독립적으로 분석 보고서를 생성하여 리팩토링 범위 결정의 근거 제공.

### 5-2. 결정 단계: AskUserQuestion으로 사용자 의사결정 (16:58)

AI가 사용자에게 **선택지를 제시**하고 사용자가 결정:

| AI 질문 | 선택지 | 사용자 선택 |
|---------|--------|------------|
| 리팩토링 범위? | Phase 1-3만 / 전체(1-7) | **전체 (Phase 1-7)** |
| VO 적용 방식? | JPA Converter(권장) / Embeddable / 일반 필드 | **JPA Converter** |
| JWT 인증 필터 구현? | 이번에 구현 / 다른 브랜치에서 | **다른 브랜치에서** |

### 5-3. 실행 단계: 6-Phase 리팩토링

AI가 결정에 따라 6단계로 리팩토링 수행:

| Phase | 작업 내용 | 변경 |
|-------|----------|------|
| 1 | 테스트 인프라 정비 — TestContainerConfig 삭제, IntegrationTestBase 개선 | 삭제+수정 |
| 2 | 예외 처리 계층 분리 — `EnrollmentException` 1개 → `StudentException`, `CourseException`, `LockException`, `ProfessorException`, `QueueException` 5개로 | 신규 5파일 |
| 3 | VO 활용 강화 — `CourseCode`, `ProfessorNo` 신규 생성, JPA Converter 4개 추가 | 신규 6파일 |
| 4 | DTO 패턴 일관성 — `CourseRequest`, `ProfessorRequest`, `StudentRequest` 추가 | 신규 3파일 |
| 5 | 설정 파일 최적화 — `application-local.yml`, `application-prod.yml` 환경별 분리 | 신규 2파일 |
| 6 | 테스트 커버리지 확대 — Controller, Repository, OpenAPI 테스트 추가 | 신규 5파일 |

### AI 활용 방식
- **분석 → 의사결정 → 실행** 3단계 워크플로우
- 분석 단계에서 **병렬 에이전트**를 활용하여 다각도 분석
- 의사결정 단계에서 **AskUserQuestion**으로 사용자 개입 — AI가 독단적으로 방향을 정하지 않음
- 결정 후 AI가 자율적으로 6단계 리팩토링 수행

### 커밋
```
b3499b0 17:32:49 [refactor] Value Object 적용 및 설정 파일 분리 (44파일, +921줄/-185줄)
```

---

## 6. E2E 검증 및 에러 대응 (17:14~17:32)

### 사용자 프롬프트
```
"메인 애플리케이션 구동하여 현재까지 작업내용 e2e 테스트 수행해"
```

### AI 수행 작업
1. Docker 컨테이너 상태 확인 (MySQL, Redis 정상 구동 확인)
2. Spring Boot 애플리케이션 구동
3. API 엔드포인트별 호출 테스트
4. 설정 파일 정합성 검증

### 문서 생성 지시 (별도 프롬프트)
```
"설계 문서 및 API 문서를 docs 디렉토리에 추가"
"README.md에 빌드/실행/접속 정보 충족 여부 확인 및 개편"
"강좌 목록 조회 응답 필수 필드 점검 및 강의 시간 포매팅 적용"
```

### AI 생성 문서
| 파일 | 내용 | 규모 |
|------|------|------|
| `docs/REQUIREMENTS.md` | 요구사항 분석, 설계 결정, ERD, 테스트 전략 | 413줄 |
| `docs/openapi.json` | OpenAPI 3.0 스펙 (JSON) | 714줄 |
| `docs/openapi.yaml` | OpenAPI 3.0 스펙 (YAML) | 422줄 |
| `docs/spec.md` | User Story + Acceptance Scenario | 158줄 |
| `README.md` | 빌드/실행/Swagger UI 접속 정보 | 47줄 |

### 커밋
```
d6fd198 17:32:41 [docs] 요구사항 및 API 명세 문서 작성 (7파일, +1,957줄)
```

---

## 7. 테스트 정비 및 설계 고민 추가 (17:47)

### 사용자 프롬프트
```
"REQUIRED_SPEC.md에 설계 고민과 이유 추가"
```

### AI 수행 작업
- `EnrollmentControllerTest.kt`, `StudentControllerTest.kt` 수정
- `REQUIRED_SPEC.md`에 설계 고민 섹션 추가 (동시성/시간표 충돌/학점 제한 관점)
- `initial-data-doc.md`에 데이터 구축 요구사항 상세 기술 (56줄)
- `history.md` 개편

### 커밋
```
67dc924 17:47:52 [refactor] 테스트 코드 정비 및 문서 업데이트 (7파일, +231줄/-216줄)
```

---

## 8. 커밋 히스토리 정리 — 사용자 주도 (17:35)

### 사용자 프롬프트
```
"현재까지 커밋내용 확인하고 커밋. 만약 이전 커밋과 합칠 수 있으면 스쿼시."
```

### AI 질문 → 사용자 결정
- AI: "스쿼시를 진행할까요? (git reset --soft 사용, interactive rebase 불가)"
- 사용자: "진행해"

### 결과
AI가 `git reset --soft`로 커밋을 7개 논리적 단위로 재정리.

---

## 9. 초기 데이터 생성 — Q&A + Plan-First (17:54)

### 사전 Q&A 단계

#### 사용자 프롬프트
```
"prompts/initial-data-doc 따라 springbootmain 실행시 초기 데이터 삽입되도록 하고 싶다면?"
```

#### AI가 7개 질문을 던지고 사용자가 모두 선택
| AI 질문 | 사용자 선택 |
|---------|------------|
| 초기 데이터 생성 시점 | 애플리케이션 시작 후 1회 |
| 적용 프로파일 | local + test |
| 중복 방지 방식 | 버전 테이블 |
| 수강신청 데이터 포함? | 일부 생성 |
| 랜덤 시드 관리 | 환경변수로 지정 |
| 샘플 데이터 소스 | 코드 내 소형 토큰 |
| /health 응답 방식 | Actuator readiness 전환 |

### 구현 단계

#### 사용자 프롬프트
```
"Implement the following plan: # 데이터 초기화 구현 계획 ..."
```

4단계(MVP)와 동일한 **Plan-First 패턴** — 사용자가 구현 계획을 작성하여 AI에게 전달.

#### AI 생성물 (8개 신규 파일)
```
src/main/kotlin/org/example/msstest/initializer/
├── InitializerProperties.kt   (13줄) — 초기화 설정 클래스
├── DataTokens.kt              (127줄) — 한국어 토큰 (학과 12개, 성씨 20개, 이름 40개 등)
├── BatchInsertExecutor.kt     (143줄) — JDBC batchUpdate 배치 삽입
├── DataInitializer.kt         (85줄) — ApplicationRunner + @Profile("local","dev")
└── generator/
    ├── ProfessorGenerator.kt  (50줄)
    ├── StudentGenerator.kt    (55줄)
    ├── CourseGenerator.kt     (75줄)
    └── ScheduleGenerator.kt   (66줄)
```

데이터 규모: 학과 12개, 교수 100명, 강좌 500개, 학생 10,000명

### 커밋
```
2429635 17:55:55 [feat] 테스트용 초기 데이터 자동 생성 기능 구현 (15파일, +1,104줄/-315줄)
```

---

## 10. 검증 및 에러 대응 (17:56)

### 사용자 프롬프트
```
"현재까지 작업 검증하기 위해 메인 애플리케이션과 테스트 수행하고 결과 알려주고"
```

### 발견된 문제
- `@JvmInline value class`와 JPA `AttributeConverter` 간 **ClassCastException** 발생
- `ProfessorNoConverter`에서 타입 변환 실패

### AI 대응
VO 4개 클래스의 `@JvmInline value class`를 `data class`로 변경:
- `ProfessorNo`, `StudentNo`, `CourseCode`, `Credits`

### 검증 결과
- 메인 애플리케이션 정상 구동
- `/health` → 200 OK
- `/api/v1/students` → 200 OK (데이터 반환)
- `/api/v1/courses` → 200 OK (데이터 반환)

### AI 활용 방식
- **"구현 → 검증 지시 → AI가 에러 발견 → AI가 수정 → 재검증"** 루프
- 사용자는 검증 지시만 내리고, AI가 에러 원인 분석부터 수정, 재검증까지 자율 수행

### 커밋
```
bfe8062 17:57:24 [docs] 작업 이력 업데이트 - VO 클래스 JPA 호환성 수정 기록
```

---

## 11. history.md 개편 (17:38~17:57)

### 사용자 프롬프트
```
"history.md 파일 읽고, 에이전트와 어떻게 상호작용했는지 위주로 시간순 정렬하여 개편해줘"
```
```
"1. 요청 내용은 가능한 사용자가 무슨 요청을 했는지 그대로 서술(문어체 구어체 상관없이 있는 그대로)
2. 기록에 있는 경우 시간을 분/초단위까지 명시"
```

### AI 작업
세션 로그(.jsonl)에서 타임스탬프를 추출하여 history.md를 시간순 재구성.

### 커밋
```
7c011c7 17:57:54 [docs] 기록 최신화
```

---

## AI 활용 전략 종합

### 반복된 패턴 3가지

#### 패턴 1: Plan-First (계획 우선 일괄 구현)
```
사용자가 구현 계획 작성 → "Implement the following plan:" → AI가 일괄 구현
```
- 사용 횟수: **2회** (MVP 구현, 데이터 초기화)
- 특징: 사용자가 "무엇을"과 "어떤 순서로"를 정하고, AI가 "어떻게"를 수행
- 17분 만에 49파일/2,667줄 생성 (MVP)

#### 패턴 2: 분석-결정-실행 (리팩토링)
```
AI 병렬 분석 → AskUserQuestion으로 사용자 결정 → AI가 실행
```
- 사용 횟수: **1회** (리팩토링)
- 특징: AI가 분석하되 방향은 사용자가 결정

#### 패턴 3: 검증 루프 (E2E 테스트)
```
구현 완료 → "e2e 테스트 수행해" → AI가 에러 발견/수정 → 재검증
```
- 사용 횟수: **2회** (MVP 후, 데이터 초기화 후)
- 특징: 사용자는 검증 지시만, AI가 에러 진단~수정~재검증까지 자율 수행

### 사용자 vs AI 역할 분담

| 사용자 (설계/의사결정) | AI (구현/실행) |
|---|---|
| CLAUDE.md 규칙 정의 (harness 설계) | 전체 소스코드 생성 (96파일, 6,400+줄) |
| REQUIRED_SPEC.md 요구사항 분석 | 인프라 설정 (Docker, application.yml) |
| 구현 계획(Implementation Plan) 작성 | 테스트 코드 작성 (단위+통합+동시성) |
| 리팩토링 방향/범위 결정 (AskUserQuestion) | 설계 문서/API 명세 작성 |
| 커밋 분리/스쿼시 결정 | 에러 진단 및 수정 (ClassCastException) |
| 검증 지시 | E2E 테스트 수행 및 결과 보고 |

### 사용자 개입 지점 (의사결정 포인트)

| 시점 | 의사결정 내용 | 방식 |
|------|-------------|------|
| 15:53 | 코딩 규칙/아키텍처 패턴 정의 | CLAUDE.md 직접 작성 |
| 16:27 | 요구사항 분석 및 설계 근거 | REQUIRED_SPEC.md 직접 작성 |
| 16:31 | 구현 순서/범위 결정 | Implementation Plan 직접 작성 |
| 16:58 | 리팩토링 범위/VO 방식/JWT 범위 | AskUserQuestion 응답 |
| 17:35 | 커밋 스쿼시 여부 | AI 질문에 "진행해" 응답 |
| 17:54 | 데이터 초기화 7개 설정 결정 | AskUserQuestion 응답 |

### 코드 규모 타임라인

| 시간 | 작업 | 규모 | 누적 |
|------|------|------|------|
| 15:56 | 프로젝트 초기화 | +523줄 | 523 |
| 15:53 | HealthCheck API | +91줄 | 614 |
| 16:27 | 문서 정비 | +80줄 | 694 |
| 16:48~49 | **MVP 전체** | **+2,667줄** | 3,361 |
| 17:32 | 리팩토링 | +921/-185줄 | 4,097 |
| 17:32 | 문서 작성 | +1,957줄 | 6,054 |
| 17:47 | 테스트 정비 | +231/-216줄 | 6,069 |
| 17:55 | 데이터 초기화 | +1,104/-315줄 | 6,858 |
| 17:57 | 기록 정리 | +79줄 | 6,937 |

### 특이 사항
1. **2시간 만에 96파일/6,400+줄** 완성 — AI 일괄 구현의 생산성
2. **main 브랜치 직접 커밋** — CLAUDE.md 규칙(feature 브랜치 사용)과 불일치, 시간 제약 추정
3. **MVP 커밋 간격 8~10초** — AI가 한번에 생성 후 사용자가 커밋만 분리
4. **speckit 시도 → 미채택** — `.speckit/` 디렉토리 없음, 사용자가 직접 스펙 문서 작성으로 대체
5. **프롬프트 파일 3종 활용** — heathCheck.md, REQUIRED_SPEC.md, initial-data-doc.md를 AI 입력 문서로 사용
