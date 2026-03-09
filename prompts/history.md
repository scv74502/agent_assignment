# 수강신청 시스템 개발 이력

> 에이전트와의 상호작용 기록 (시간순)

---

## 2026-02-08

### 16:31:40 (UTC+9) - MVP 구현 시작
**요청**: "Implement the following plan: # Implementation Plan: 수강신청 시스템 ..."
**작업**:
- Phase 1 (Setup): build.gradle.kts 의존성, docker-compose.yml, application.yml
- Phase 2 (Foundational): Config, Entity, Repository, Exception, Redis 인프라
- Phase 3 (수강신청 API): EnrollmentService, 동시성 제어 (분산 락 + DB 락)
- Phase 4-5 (조회 API): Course, Student, Professor, Timetable API

---

### 16:48:32 (UTC+9) - 리팩토링 계획 요청
**요청**: "현재 디렉토리 구조가 클린하지 않아 엉망인데 리팩토링 계획을 세워봐"
**작업**: 탐색 에이전트 3개 병렬 실행 (설정파일/디렉토리구조/테스트코드 분석)

---

### 16:49:28 ~ 16:55:24 (UTC+9) - 분석 보고서 생성
**작업**:
- 설정 파일 분석 보고서 (application.yml, docker-compose.yml, Config 클래스)
- 디렉토리 구조 및 아키텍처 분석 (계층별 역할, 일관성 문제점)
- 테스트 코드 구조 분석 (커버리지 격차, 설정 중복)

---

### 16:58:19 (UTC+9) - 리팩토링 범위 결정 (Q&A)
**에이전트 질문**:
- "어느 범위까지 리팩토링을 진행할까요?"
- "Value Object를 Entity에 어떻게 적용할까요?"
- "JWT 인증 필터를 구현할까요?"

**사용자 응답**:
| 질문 | 선택 |
|------|------|
| 리팩토링 범위 | 전체 (Phase 1-7) |
| VO 적용 방식 | JPA Converter 사용 (권장) |
| JWT 인증 필터 | 다른 브랜치에서 구현 예정 |

---

### 17:14:46 (UTC+9) - E2E 테스트 요청
**요청**: "메인 애플리케이션 구동하여 현재까지 작업내용 e2e 테스트 수행해"
**작업**:
- Docker 컨테이너 상태 확인 (MySQL, Redis 정상 구동)
- 애플리케이션 구동 및 API 엔드포인트 테스트
- 설정 파일 검증

---

### 17:35 (UTC+9) - 커밋 정리
**요청**: "현재까지 커밋내용 확인하고 커밋. 만약 이전 커밋과 합칠 수 있으면 스쿼시."
**에이전트 질문**: "스쿼시를 진행할까요? (git reset --soft 사용, interactive rebase 불가)"
**사용자 응답**: "진행해"

**최종 커밋 히스토리**:
```
b3499b0 [refactor] Value Object 적용 및 설정 파일 분리
d6fd198 [docs] 요구사항 및 API 명세 문서 작성
15bb2fc [test] 단위 테스트 및 통합 테스트 구현
e722522 [feat] 조회 API 구현
7b75a94 [feat] 수강신청 API 구현
9d5178f [feat] 기반 코드 구현
7dabb82 [feat] 프로젝트 설정 및 인프라 구성
```

---

### 17:38 (UTC+9) - history.md 개편
**요청**: "history.md 파일 읽고, 에이전트와 어떻게 상호작용했는지 위주로 시간순 정렬하여 개편해줘"
**추가 요청**: "1. 요청 내용은 가능한 사용자가 무슨 요청을 했는지 그대로 서술(문어체 구어체 상관없이 있는 그대로) 2. 기록에 있는 경우 시간을 분/초단위까지 명시"
**작업**: 세션 로그(.jsonl)에서 타임스탬프 추출, 사용자 원문 요청 반영

---

### 17:54 (UTC+9) - 데이터 초기화 구현
**요청**: "Implement the following plan: # 데이터 초기화 구현 계획 ..."
**작업**:
- InitializerProperties: 초기화 설정 클래스 (enabled, 엔티티 수량, 배치 크기)
- DataTokens: 토큰 목록 (학과명 12개, 성씨 20개, 이름글자 40개, 강좌 접두사/유형, 건물명, 시간대)
- Generator 클래스: ProfessorGenerator, StudentGenerator, CourseGenerator, ScheduleGenerator
- BatchInsertExecutor: JDBC batchUpdate 기반 배치 삽입 (배치 크기 1000)
- DataInitializer: ApplicationRunner 구현, @Profile("local", "dev"), 중복 실행 방지
- application-local.yml: initializer 설정 추가, rewriteBatchedStatements=true

**생성 파일**:
```
src/main/kotlin/org/example/msstest/initializer/
├── InitializerProperties.kt
├── DataTokens.kt
├── BatchInsertExecutor.kt
├── DataInitializer.kt
└── generator/
    ├── ProfessorGenerator.kt
    ├── StudentGenerator.kt
    ├── CourseGenerator.kt
    └── ScheduleGenerator.kt
```

**데이터 규모**: 교수 100명, 강좌 500개, 학생 10,000명, 학과 12개

---

## 이전 기록 (시간 미상)

1. **요청**: "healthCheck.md로 엔드포인트 인터페이스 구조 제시 및 초기 동작 테스트"
   **작업**: HealthCheckApi 인터페이스 생성, HealthCheckController 구현

2. **요청**: "speckit 활용하여 스펙 주도 개발 설정"
   **작업**: speckit 설정 완료

3. **요청**: "initial-data-doc.md 기반 초기 데이터 생성 로직 고민"
   **응답**: 요구사항 정리 및 데이터 생성 필요성 확인

4. **요청**: "강좌 목록 조회 응답 필수 필드 점검 및 강의 시간 포매팅 적용"
   **작업**: CourseResponse에 schedule 문자열 포맷 추가, OpenAPI 스키마 갱신

5. **요청**: "README.md에 빌드/실행/접속 정보 충족 여부 확인 및 개편"
   **작업**: README.md 신규 작성 (빌드/실행/포트/Swagger UI 포함)

6. **요청**: "설계 문서 및 API 문서를 docs 디렉토리에 추가"
   **작업**: docs/spec.md, docs/openapi.yaml 생성

7. **요청**: "현재 애플리케이션 구동시 초기 데이터가 삽입되도록 설정되어 있나 체크해봐"
   **응답**: 초기 데이터 삽입 로직 미설정 확인 (data.sql/import.sql 없음, Runner 없음)

8. **요청**: "prompts/initial-data-doc 따라 springbootmain 실행시 초기 데이터 삽입되도록 하고 싶다면?"
   **에이전트 질문 (Q&A)**:
   | 질문 | 선택 |
   |------|------|
   | 초기 데이터 생성 시점 | 애플리케이션 시작 후 1회 |
   | 적용 프로파일 | local + test |
   | 중복 방지 방식 | 버전 테이블 |
   | 수강신청 데이터 | 일부 생성 |
   | 랜덤 시드 | 환경변수로 지정 |
   | 샘플 데이터 소스 | 코드 내 소형 토큰 |
   | /health 응답 방식 | Actuator readiness 전환 |

9. **요청**: "REQUIRED_SPEC.md에 설계 고민과 이유 추가"
   **작업**: 동시성/시간표 충돌/학점 제한 관점의 설계 이유 섹션 추가

10. **요청**: 수강신청 API 디렉토리 구조 리팩토링 (Phase 1-6)
    **작업**:
    - Phase 1: 테스트 인프라 정비 (TestContainerConfig 삭제, IntegrationTestBase 개선)
    - Phase 2: 예외 처리 계층 분리 (StudentException, CourseException, LockException 등)
    - Phase 3: Value Object 활용 강화 (JPA Converter, @Convert 적용)
    - Phase 4: DTO 패턴 일관성 (Request DTO 생성)
    - Phase 5: 설정 파일 최적화 (환경별 분리)
    - Phase 6: 테스트 커버리지 확대 (Controller/Repository 테스트)

---

### 17:56 (UTC+9) - VO 클래스 JPA 호환성 수정
**요청**: "현재까지 작업 검증하기 위해 메인 애플리케이션과 테스트 수행하고 결과 알려주고"
**문제 발견**:
- `@JvmInline value class`와 JPA AttributeConverter 간 ClassCastException 발생
- ProfessorNoConverter에서 타입 변환 실패

**해결**:
- VO 클래스들의 `@JvmInline value class`를 `data class`로 변경
  - ProfessorNo, StudentNo, CourseCode, Credits

**검증**:
- 메인 애플리케이션 정상 구동 확인
- `/health` → 200 OK
- `/api/v1/students` → 200 OK (데이터 반환)
- `/api/v1/courses` → 200 OK (데이터 반환)

---

## 2026-03-03

### 강좌 조회 API 개편: 커서 페이지네이션 + 학과/과목유형 필터링
**요청**: "강좌 조회에서 학과별/과목유형별 조회, 커서 기반 pageable, API 문서 겸 인터페이스와 도메인/엔티티/테스트 수정"

**설계 결정** (사용자 확인):
- 과목유형: 전공필수/전공선택/교양필수/교양선택 (4분류)
- 학과: Course 엔티티에 department 필드 직접 추가
- 커서 키: Course ID (Long, auto-increment)
- 적용 범위: GET /courses + GET /courses/available

**작업**:
- Phase 1: `CourseType` enum 생성, `Course` 엔티티에 `courseType`, `department` 필드 추가
- Phase 2: `CourseSpecifications` 유틸리티 생성 (JPA Specification 조합), `CourseRepository`에 `JpaSpecificationExecutor` 추가, DataInitializer 업데이트
- Phase 3: `CursorPageResponse<T>` 커서 페이지 래퍼 DTO 생성, `CourseResponse`/`CreateCourseRequest`에 필드 추가
- Phase 4-5: `CourseService`에 `getAllCoursesPaged`/`getAvailableCoursesPaged` 메서드 추가, `CourseApi`/`CourseController` 시그니처 변경, `GlobalExceptionHandler`에 `MethodArgumentTypeMismatchException` 핸들러 추가
- Phase 6: 기존 테스트의 `Course.create()` 호출 전부 수정 (courseType, department 파라미터 추가)
- Phase 7: `CourseTypeTest`, `CourseControllerTest`, `CourseServiceTest` 신규 테스트 작성

**검증**:
- 컴파일 성공 (`compileKotlin`, `compileTestKotlin`)
- 단위 테스트 통과 (CourseTypeTest, CourseControllerTest, CourseScheduleTest, CreditsTest)
- 통합 테스트는 Docker 실행 후 별도 확인 필요

---

### 테스트 데이터 격리 개선: @Transactional 자동 롤백 도입
**요청**: "테스트 데이터 격리를 @Transactional 롤백 방식으로 개선"

**설계 결정**:
- `IntegrationTestBase`에 `@Transactional` 추가 → 각 테스트 종료 시 자동 롤백
- 동시성 테스트(`EnrollmentServiceConcurrencyTest`)는 `@Transactional(propagation = NOT_SUPPORTED)`로 예외 처리
- `MssTestApplicationTests`, `OpenApiGeneratorTest`를 `IntegrationTestBase` 상속으로 전환

**작업**:
- `IntegrationTestBase`에 `@Transactional` 어노테이션 추가
- `StudentRepositoryTest`: `@BeforeEach setup()` (deleteAll) 제거
- `EnrollmentRepositoryTest`: `deleteAll()` 4줄 제거, save()만 유지
- `EnrollmentServiceTest`: `deleteAll()` 5줄 제거, save()만 유지
- `CourseServiceTest`: `deleteAll()` 제거, save()만 유지
- `EnrollmentServiceConcurrencyTest`: `@Transactional(propagation = NOT_SUPPORTED)` 추가, `@AfterEach cleanup()` 추가
- `MssTestApplicationTests`: `IntegrationTestBase()` 상속, `@SpringBootTest` 제거
- `OpenApiGeneratorTest`: `IntegrationTestBase()` 상속, `@SpringBootTest` 제거

**검증**:
- 컴파일 성공 (`compileTestKotlin`)
- 변경된 통합 테스트 전체 통과 (StudentRepositoryTest, EnrollmentRepositoryTest, EnrollmentServiceTest, CourseServiceTest, MssTestApplicationTests, OpenApiGeneratorTest, 동시성 테스트 1번)
- 기존 실패 2건은 본 변경과 무관: StudentControllerTest(엔드포인트 경로 불일치), 동시성 테스트 2번(Redis 락 타이밍)

---

### 커밋 및 버그 수정
**요청**: "현재 변경사항 커밋 준비"

**테스트 실행 중 발견한 버그 수정**:
1. **수강신청 동시성 L1 캐시 충돌** (`EnrollmentService.enroll()`):
   - 원인: `findById()` → L1 캐시에 version=0 로드 → `findByIdWithLock()` → L1 캐시 엔티티 반환 → `save()` 시 `WHERE version=0` 조건이 갱신된 DB와 불일치 → `ObjectOptimisticLockingFailureException`
   - 수정: lock 전 `findById()` → `existsById()`로 변경, credit/schedule 검증을 lock 내부로 이동
2. **분산락 타임아웃** (`RedisLockService`):
   - 원인: `waitTime=5s`, `leaseTime=3s`로 다수 스레드 직렬 처리에 부족
   - 수정: 기본값 `waitTime=30s`, `leaseTime=30s`로 조정
3. **StudentControllerTest URL 불일치**:
   - 원인: 테스트가 `/api/v1/students/by-student-no/20240001` (PathVariable) 사용, 실제 엔드포인트는 `@GetMapping(params = ["studentNo"])` (RequestParam)
   - 수정: `get("/api/v1/students").param("studentNo", "20240001")`로 변경

**커밋 (4건)**:
1. `[feat] 강좌 이수구분 및 커서 기반 페이지네이션 API 구현` — 소스 코드 13파일
2. `[fix] 수강신청 동시성 L1 캐시 충돌 및 분산락 타임아웃 수정` — EnrollmentService, RedisLockService
3. `[test] 테스트 데이터 격리 개선 및 강좌 API 테스트 추가` — 테스트 12파일
4. `[docs] 문서 및 설정 업데이트` — .gitignore, CLAUDE.MD, MSS_PROBLEM.MD 등 6파일

**최종 검증**: 66 tests 전체 통과 (`./gradlew test --rerun`)

---

### TestContainers 컨테이너 관리 개선
**요청**: "TestContainers reuse 컨테이너가 수동 정리 전까지 영구 존재하는 문제 해결용 Gradle 태스크 추가"

**배경**:
- `.withReuse(true)` + `testcontainers.reuse.enable=true` 설정으로 컨테이너 누적 문제는 해결됨
- 상태 초기화는 `ddl-auto: create-drop` + `clearRedis()` @BeforeEach로 이미 보장
- 남은 문제: reuse 컨테이너가 개발 세션 종료 후에도 영구 존재

**작업**:
- `build.gradle.kts`에 `cleanTestContainers` Exec 태스크 추가
- `docker ps -aq --filter label=org.testcontainers=true`로 TC 컨테이너만 식별
- 컨테이너가 없으면 메시지 출력 후 `StopExecutionException`으로 스킵
- 있으면 `docker rm -f`로 선택적 제거

**검증**:
- `./gradlew tasks --group=verification` → `cleanTestContainers` 태스크 등록 확인
- 컨테이너 없을 때 → "정리할 TestContainers 컨테이너가 없습니다." 메시지 + BUILD SUCCESSFUL
- `./gradlew test --rerun` → 66 tests 전체 통과, MySQL/Redis reuse 컨테이너 2개 생성
- `./gradlew cleanTestContainers` → TC 컨테이너 2개 제거
- `docker ps` → 개발용 `mysql_db`만 남음

---

## 2026-03-04

### 수강신청 동시성 학점 초과 레이스 컨디션 수정 및 락 타임아웃 조정
**요청**: "수강신청 동시성 학점 초과 레이스 컨디션 수정 및 락 타임아웃 조정" 플랜 구현

**문제**:
- Redis 분산락이 과목 단위(`enrollment:lock:course:{courseId}`)로만 걸려, 동일 학생이 서로 다른 2과목에 동시 수강신청하면 학점 제한(18학점) 초과 가능한 레이스 컨디션 존재
- `@Transactional`이 메서드 레벨에 있어 락 해제 후 트랜잭션 커밋되는 미세한 윈도우 존재
- waitTime=30s, leaseTime=30s는 수강신청 UX에 비해 과도

**작업**:
1. **RedisLockService.kt**: 기본 타임아웃 waitTime 30→5s, leaseTime 30→10s 조정. `enrollmentLockKey(courseId)` → `enrollmentLockKey(studentId)` 변경 (키 패턴: `enrollment:lock:student:$studentId`)
2. **EnrollmentService.kt**:
   - `enroll()`, `cancel()`에서 `@Transactional` 제거
   - 락 키를 학생 ID 기반으로 변경
   - `TransactionTemplate`을 주입받아 락 내부에서 트랜잭션 관리 (커밋 후 락 해제 보장)
   - `EnrollmentResponse.from()` 호출을 트랜잭션 블록 내부로 이동 (LazyInitializationException 방지)
   - 락 외부에 빠른 실패 검증, 락 내부에 정확한 재검증 (double-check)
3. **EnrollmentServiceConcurrencyTest.kt**: 동일 학생 학점 초과 방지 동시성 테스트 추가 (학생 1명, 15학점 수강 중, 3학점 2과목 동시 신청 → 1건 성공/1건 실패 검증)

**검증**: 67 tests 전체 통과 (기존 66 + 신규 1)

---

## 2026-03-06

### 14:59 (UTC+9) - 하드코딩 값 상수 분리 및 Redis 설정 외부화 (잔여 작업)
**요청**: 이전 세션에서 수행한 "하드코딩 값 상수 분리 및 Redis 설정 외부화" 작업의 잔여 4개 항목 완료 및 커밋

**작업**:
1. `EnrollmentQueueService.kt:37` - `Duration.ofHours(1)` → `Duration.ofHours(queueTtlHours)` 변경
2. `.env.example` - `REDIS_LOCK_WAIT_TIME`, `REDIS_LOCK_LEASE_TIME`, `ENROLLMENT_QUEUE_TTL_HOURS` 섹션 추가
3. `application.yml` - `redis.lock.wait-time`, `redis.lock.lease-time`, `enrollment.queue-ttl-hours` 환경변수 바인딩 추가
4. `application-test.yml` - `redis.lock.wait-time: 5`, `redis.lock.lease-time: 10`, `enrollment.queue-ttl-hours: 1` 추가

**검증**:
- `./gradlew compileKotlin` - BUILD SUCCESSFUL
- `./gradlew test` - BUILD SUCCESSFUL (ktlintCheck 태스크는 프로젝트에 미설정)

**커밋**: `c2c0a3d [refactor] 하드코딩 값 상수 분리 및 Redis 설정 외부화`

**참고**: 잔여 작업 확인 결과 이전 세션에서 이미 모든 변경이 커밋 완료된 상태였음

---

### 16:40 (UTC+9) - TestContainer 최적화: BuildService + jdbc:tc: 전환
**요청**: "TestContainer 최적화 계획 구현 — MySQL을 jdbc:tc: URL로, Redis를 Gradle BuildService로 전환하고, IDE fallback용 singleton 패턴 적용"

**작업**:
- **MySQL**: `IntegrationTestBase`에서 `MySQLContainer` 수동 생성 제거, `application-test.yml`에 `jdbc:tc:mysql:8.0:///mss_test` + `ContainerDatabaseDriver` 설정으로 자동 관리 위임
- **Redis**: Gradle `BuildService`(`buildSrc/TestContainerService.kt`)로 수명주기 관리, IDE 직접 실행 시 `TestContainerSingletons` singleton fallback
- **IntegrationTestBase**: `@Testcontainers` 제거, `ContainerConfig` constructor 파라미터 + `resolveContainerConfig()` (system property → BuildService 경로, 없으면 → singleton fallback)
- **build.gradle.kts**: `TestContainerService` BuildService 등록, `tasks.withType<Test>`에서 Redis host/port를 `systemProperty`로 주입, `cleanTestContainers` 태스크 제거 (BuildService가 자동 정리), `testcontainers:jdbc` 의존성 추가

**신규 생성 파일**:
- `buildSrc/build.gradle.kts` — BuildService용 빌드 설정
- `buildSrc/src/main/kotlin/TestContainerService.kt` — Redis 컨테이너를 BuildService로 관리
- `src/test/kotlin/org/example/msstest/ContainerConfig.kt` — Redis 접속 정보 VO
- `src/test/kotlin/org/example/msstest/TestContainerSingletons.kt` — IDE fallback용 Redis singleton

**검증**: `./gradlew test` — BUILD SUCCESSFUL (전체 테스트 통과)