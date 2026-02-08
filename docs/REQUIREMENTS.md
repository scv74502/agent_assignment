# 수강신청 시스템 요구사항 및 설계 결정 문서

**Feature**: 001-course-registration
**Created**: 2026-02-08
**Last Updated**: 2026-02-08

---

## 1. 프로젝트 개요

### 1.1 목적
고성능 수강신청 시스템 구현. 핵심 목표는 **동시성 제어(Concurrency Control)**으로 정합성을 유지하고  

### 1.2 핵심 요구사항
- 1,000명 이상의 동시 사용자 처리(트래픽)
- 정원 1명 남은 상태에서 100명 동시 신청 시 정확히 1명만 성공
- 3초 이내 응답 보장
- 트랜잭션 ACID 보장

### 1.3 기술 스택
| 구분 | 기술 | 버전 |
|------|------|------|
| Language | Kotlin | 1.9.25 |
| Runtime | Java | 21 |
| Framework | Spring Boot | 3.5.10 |
| ORM | Spring Data JPA | - |
| Auth | Spring Security + JWT | - |
| API Docs | SpringDoc OpenAPI | - |
| Database | MySQL | 8.0 |
| Cache/Lock | Redis | 7.0 |
| Distributed Lock | Redisson | 3.27.0 |
| Testing | TestContainers | 1.19.7 |

---

## 2. 기능 요구사항 (Functional Requirements)

### 2.1 수강신청 (US1 - P1)
| ID | 요구사항 | 우선순위 |
|----|----------|----------|
| FR-007 | 학생은 원하는 강좌를 수강신청할 수 있어야 한다 | P1 |
| FR-009 | 동시에 여러 학생이 같은 강좌를 신청해도 정원을 초과하지 않도록 보장 | P1 |
| FR-010 | 정원이 1명 남은 상태에서 다수가 동시에 신청해도 정확히 1명만 성공 | P1 |

### 2.2 수강취소 (US2 - P2)
| ID | 요구사항 | 우선순위 |
|----|----------|----------|
| FR-008 | 학생은 신청한 강좌를 수강취소할 수 있어야 한다 | P2 |

### 2.3 강좌 조회 (US3 - P3)
| ID | 요구사항 | 우선순위 |
|----|----------|----------|
| FR-003 | 시스템은 전체 강좌 목록을 조회할 수 있어야 한다 | P3 |
| FR-005 | 강좌 정보에는 강좌명, 교수명, 학점, 강의 시간, 정원, 현재 신청 인원 포함 | P3 |

### 2.4 시간표 조회 (US4 - P4)
| ID | 요구사항 | 우선순위 |
|----|----------|----------|
| FR-002 | 학생은 자신의 시간표(신청한 강좌 목록)를 조회할 수 있어야 한다 | P4 |

### 2.5 학생/교수 조회 (US5 - P5)
| ID | 요구사항 | 우선순위 |
|----|----------|----------|
| FR-001 | 시스템은 등록된 학생 목록을 조회할 수 있어야 한다 | P5 |
| FR-006 | 시스템은 등록된 교수 목록을 조회할 수 있어야 한다 | P5 |

---

## 2-1 기능별 요구사항 충족 위한 설계
1. 여러 명이 몰리는 상황 - Redis를 통해 대기열 구현
2. 데이터 정합성
- 
3. 인증/인가 - 추후 구현(교수 ROLE이 있어야 )

## 3. 비즈니스 규칙 (Business Rules)

### 3.1 학점 제한
| ID | 규칙 | 근거 |
|----|------|------|
| FR-011 | 학생당 한 학기 최대 **18학점**까지만 신청 가능 | 일반 대학 학사 규정 기준 |

### 3.2 시간 중복 검사
| ID | 규칙 | 근거 |
|----|------|------|
| FR-012 | 이미 신청한 강좌와 **1분이라도 겹치면** 신청 거부 | 물리적 수업 참석 불가 |
| FR-012-1 | **경계 시간은 중복으로 간주하지 않음** (11:00 종료 ↔ 11:00 시작 = 허용) | 이동 시간 0분 가정, 연속 수업 허용 |

### 3.3 정원 관리
| ID | 규칙 | 근거 |
|----|------|------|
| FR-013 | 현재 수강 인원이 정원에 도달하면 신청 거부 | 강의실 물리적 제약 |
| FR-014 | 이미 신청한 강좌 재신청 시 거부 | 중복 데이터 방지 |

---

## 4. 설계 결정 사항 (Design Decisions)

### 4.1 시간표 슬롯 정의

#### 결정
| 항목 | 값 | 근거 |
|------|-----|------|
| 요일 범위 | 월요일 ~ 금요일 (5일) | 일반 대학 주간 강의 기준 |
| 수업 시간 범위 | 08:00 ~ 22:00 | 야간 수업 포함 (대학원 고려) |
| 시간 단위 | 분(minute) 단위 | 30분/50분/75분 등 다양한 수업 시간 지원 |
| 교시 개념 | **사용하지 않음** | 유연한 시간 배치 지원 |

#### 시간 표현 방식
```kotlin
data class CourseSchedule(
    val dayOfWeek: DayOfWeek,  // MONDAY, TUESDAY, ...
    val startTime: LocalTime,  // 09:00
    val endTime: LocalTime     // 10:30
)
```

#### 근거
- 교시 기반 시스템은 학교마다 다름 (50분/75분/90분)
- 시작/종료 시간 직접 지정이 더 유연하고 범용적
- `java.time.DayOfWeek`, `java.time.LocalTime` 표준 사용으로 국제화 용이

---

### 4.2 학점 분포 및 범위

#### 결정
| 항목 | 값 | 근거 |
|------|-----|------|
| 강의당 학점 범위 | **1 ~ 6학점** | 교양 1학점 ~ 졸업논문 6학점 |
| 학기당 최대 학점 | **18학점** | 일반 대학 학사 규정 |
| 학점 타입 | 정수 (Int) | 0.5학점 단위 불필요 |

#### Value Object 구현
```kotlin
data class Credits(val value: Int) {
    init {
        require(value in 1..6) { "학점은 1~6 범위여야 합니다: $value" }
    }
}
```

> **Note**: JPA AttributeConverter와의 호환성을 위해 `@JvmInline value class` 대신 `data class`를 사용합니다.

#### 학점별 일반적 수업 시간
| 학점 | 주당 수업 시간 | 예시 |
|------|---------------|------|
| 1학점 | 1시간 | 세미나, 특강 |
| 2학점 | 2시간 | 실험, 실습 보조 |
| 3학점 | 3시간 | 일반 전공/교양 |
| 4학점 | 4시간 | 심화 전공 |
| 5-6학점 | 5-6시간 | 졸업논문, 인턴십 |

---

### 4.3 시간 충돌 판정 로직

#### 결정
```
기존 수업: [startA, endA)
신규 수업: [startB, endB)

충돌 조건: startA < endB AND startB < endA
허용 조건: endA <= startB OR endB <= startA
```

#### 경계 케이스 처리
| 기존 수업 | 신규 수업 | 결과 | 근거 |
|-----------|-----------|------|------|
| 09:00-10:00 | 10:00-11:00 | **허용** | 종료 시간 = 시작 시간 (이동 시간 0분) |
| 09:00-10:30 | 10:00-11:00 | **거부** | 30분 중복 |
| 09:00-10:00 | 09:30-10:30 | **거부** | 30분 중복 |
| 월 09:00-10:00 | 화 09:00-10:00 | **허용** | 요일이 다름 |

#### 구현 코드
```kotlin
fun hasTimeConflict(existing: CourseSchedule, new: CourseSchedule): Boolean {
    if (existing.dayOfWeek != new.dayOfWeek) return false
    return existing.startTime < new.endTime && new.startTime < existing.endTime
}
```

#### 근거
- **반개구간 [start, end)** 사용: 수학적으로 연속 구간 표현에 적합
- 경계 허용: 대학에서 연속 수업이 일반적 (10:00 종료 → 10:00 시작)
- 이동 시간: 동일 건물 가정, 별도 여유 시간 미적용

---

### 4.4 동시성 제어 전략

#### 결정: 3중 잠금 전략

| 계층 | 기술 | 역할 | 범위 |
|------|------|------|------|
| 1차 | Redis 분산 락 (Redisson) | 요청 직렬화 | 강좌 ID 기준 |
| 2차 | JPA Pessimistic Lock | DB 행 잠금 | Course 레코드 |
| 3차 | JPA @Version | 낙관적 충돌 감지 | 최종 방어선 |

#### 동일 학생 중복/동시 신청 처리

| 시나리오 | 처리 방식 | 근거 |
|----------|-----------|------|
| 동일 학생이 같은 강좌 2회 신청 | DB Unique 제약조건으로 거부 | `(student_id, course_id)` 유니크 인덱스 |
| 동일 학생이 동시에 2개 강좌 신청 | **각각 독립 처리** | 강좌별 분산 락 (학생 기준 락 없음) |
| 동일 학생이 같은 강좌 동시 신청 | Redis 분산 락 + DB 유니크로 1건만 성공 | 락 획득 실패 시 대기 후 재시도 |

#### 락 키 설계
```kotlin
// 강좌 기준 락 (채택)
val lockKey = "enrollment:course:${courseId}"

// 학생 기준 락 (미채택 - 병렬성 저하)
// val lockKey = "enrollment:student:${studentId}"

// 복합 락 (미채택 - 과도한 복잡성)
// val lockKey = "enrollment:${studentId}:${courseId}"
```

#### 근거: 강좌 기준 락 채택 이유
1. **핵심 자원**: 정원(capacity)은 강좌에 귀속
2. **경쟁 지점**: 동시 신청 경쟁은 "같은 강좌"에서 발생
3. **병렬성**: 다른 강좌 신청은 동시 처리 가능
4. **단순성**: 락 키가 단일 차원

#### 락 파라미터
| 파라미터 | 값 | 근거 |
|----------|-----|------|
| waitTime | 3초 | 사용자 응답 시간 요구사항 |
| leaseTime | 5초 | 트랜잭션 완료 보장 + 여유 |
| 재시도 | 없음 | 실패 시 즉시 응답 (UX) |

---

### 4.5 학점 검증 시점

#### 결정
| 검증 시점 | 방식 | 근거 |
|-----------|------|------|
| 락 획득 전 | 현재 신청 학점 + 신규 강좌 학점 <= 18 검증 | 불필요한 락 획득 방지 |
| 락 획득 후 | 재검증 없음 | 학생 기준 락 미사용으로 동시 변경 가능성 있으나, 초과 허용보다 사용자 경험 우선 |

#### 트레이드오프 분석
- **현재 구현**: 락 획득 전 1회 검증
- **엄격한 구현**: 락 획득 후 재검증 필요 (학생 기준 락 추가 시)
- **선택 근거**: 18학점 초과는 치명적 오류 아님 (관리자 조정 가능), 성능 우선

---

### 4.6 시간 충돌 검증 시점

#### 결정
| 검증 시점 | 방식 | 근거 |
|-----------|------|------|
| 락 획득 전 | 기존 신청 강좌들의 시간과 비교 | 불필요한 락 획득 방지 |
| 락 획득 후 | 재검증 없음 | 학생 기준 락 미사용 (4.4와 동일 근거) |

---

### 4.7 에러 처리 전략

#### 결정: sealed class 기반 타입 안전 예외

```kotlin
sealed class EnrollmentException(
    val errorCode: ErrorCode,
    override val message: String
) : RuntimeException(message) {
    class StudentNotFound(studentId: Long) : EnrollmentException(...)
    class CourseNotFound(courseId: Long) : EnrollmentException(...)
    class CourseFull(courseId: Long) : EnrollmentException(...)
    class CreditLimitExceeded(current: Int, requested: Int, max: Int) : EnrollmentException(...)
    class ScheduleConflict(existingCourse: String, newCourse: String) : EnrollmentException(...)
    class AlreadyEnrolled(studentId: Long, courseId: Long) : EnrollmentException(...)
    class EnrollmentNotFound(studentId: Long, courseId: Long) : EnrollmentException(...)
    class LockAcquisitionFailed(courseId: Long) : EnrollmentException(...)
}
```

#### 근거
- **컴파일 타임 안전성**: when 표현식에서 모든 케이스 처리 강제
- **명확한 에러 분류**: HTTP 상태 코드 매핑 용이
- **디버깅 용이성**: 예외 타입별 상세 정보 포함

---

### 4.8 API 설계 패턴

#### 결정: Swagger-First (OpenAPI Interface)

```kotlin
// 1단계: OpenAPI 인터페이스 정의
interface EnrollmentApi {
    @Operation(summary = "수강신청")
    @PostMapping("/api/enrollments")
    fun enroll(@RequestBody request: EnrollmentRequest): ResponseEntity<EnrollmentResponse>
}

// 2단계: Controller 구현
@RestController
class EnrollmentController(
    private val enrollmentService: EnrollmentService
) : EnrollmentApi {
    override fun enroll(request: EnrollmentRequest): ResponseEntity<EnrollmentResponse> {
        // 구현
    }
}
```

#### 근거
- **계약 우선**: API 스펙 먼저 정의 → 프론트엔드 병렬 개발 가능
- **문서 자동화**: Swagger UI 자동 생성
- **일관성**: CLAUDE.md 규칙 준수

---

## 5. 데이터 모델

### 5.1 엔티티 관계도

```
┌─────────────┐       ┌─────────────────┐       ┌─────────────┐
│   Student   │       │   Enrollment    │       │   Course    │
├─────────────┤       ├─────────────────┤       ├─────────────┤
│ id (PK)     │──────<│ id (PK)         │>──────│ id (PK)     │
│ studentNo   │       │ studentId (FK)  │       │ name        │
│ name        │       │ courseId (FK)   │       │ credits     │
│ department  │       │ createdAt       │       │ capacity    │
│ totalCredits│       └─────────────────┘       │ currentCount│
└─────────────┘                                 │ professorId │
                                                │ version     │
                                                └──────┬──────┘
                                                       │
┌─────────────┐                              ┌─────────┴────────┐
│  Professor  │                              │  CourseSchedule  │
├─────────────┤                              ├──────────────────┤
│ id (PK)     │<─────────────────────────────│ id (PK)          │
│ name        │                              │ courseId (FK)    │
│ department  │                              │ dayOfWeek        │
└─────────────┘                              │ startTime        │
                                             │ endTime          │
                                             └──────────────────┘
```

### 5.2 핵심 제약조건

| 테이블 | 제약조건 | 타입 | 목적 |
|--------|----------|------|------|
| student | student_no | UNIQUE | 학번 중복 방지 |
| enrollment | (student_id, course_id) | UNIQUE | 중복 신청 방지 |
| course_schedule | (course_id, day_of_week, start_time) | UNIQUE | 동일 시간 중복 방지 |

---

## 6. 테스트 전략

### 6.1 단위 테스트
| 테스트 대상 | 검증 내용 |
|-------------|-----------|
| Credits Value Object | 1-6 범위 검증, 범위 초과 시 예외 |
| CourseSchedule | 시간 충돌 판정 로직 |
| StudentNo Value Object | 8자리 숫자 형식 검증 |

### 6.2 통합 테스트
| 테스트 대상 | 검증 내용 |
|-------------|-----------|
| EnrollmentService | 정상 신청/취소 플로우 |
| 동시성 테스트 | 100명 동시 신청 → 1명만 성공 |

### 6.3 테스트 인프라
- **TestContainers**: MySQL 8.0, Redis 7.0 컨테이너 자동 생성
- **@Testcontainers**: 테스트 격리 보장
- **Reusable Containers**: 테스트 속도 최적화

---

## 7. 성능 목표 및 제약

| 지표 | 목표값 | 측정 방법 |
|------|--------|-----------|
| 동시 사용자 | 1,000+ | 부하 테스트 |
| 응답 시간 | < 3초 | P99 레이턴시 |
| 정원 초과율 | 0% | 동시성 테스트 |
| 데이터 유실 | 0% | 장애 시나리오 테스트 |

---

## 8. 가정 사항 (Assumptions)

| 가정 | 설명 |
|------|------|
| 사전 등록 데이터 | 학생, 교수, 강좌 데이터는 사전 등록되어 있음 |
| 인증 시스템 | 별도 인증 시스템 통해 학생 인증 처리 |
| 수강신청 기간 | 기간 관리 없음 (항상 신청 가능 상태) |
| 주간 스케줄 | 강의 시간은 매주 반복 |

---

## 9. 향후 고려 사항 (Out of Scope)

| 항목 | 우선순위 | 비고 |
|------|----------|------|
| 학과별 필터링 | 중 | FR-004 추후 구현 |
| 수강신청 기간 관리 | 중 | 관리자 기능 필요 |
| 대기열 시스템 | 하 | 현재는 단순 실패 처리 |
| 알림 기능 | 하 | 정원 확보 시 알림 |

---

## 10. 변경 이력

| 일자 | 변경 내용 | 작성자 |
|------|-----------|--------|
| 2026-02-08 | 초안 작성 | Claude Code |
| 2026-02-08 | MVP 구현 완료 (Phase 1-5) | Claude Code |
