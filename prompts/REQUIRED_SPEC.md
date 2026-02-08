# 수강신청 시스템 기획서 (Course Registration System Specification)

## 1. 프로젝트 개요
매 학기 수강신청 기간 발생하는 트래픽 폭주 및 서버 다운 문제를 해결하고, 데이터 무결성을 보장하는 고성능 수강신청 시스템을 구축한다.
핵심 목표는 **동시성 제어(Concurrency Control)**를 통해 정원 초과 문제를 원천 차단하는 것이다.

## 2. 요구사항 분석

### 2.1 기능적 요구사항 (Functional Requirements)

#### A. 학생 (Student)
*   **학생 목록 조회**: 등록된 학생들의 정보를 조회할 수 있어야 한다.
*   **내 시간표 조회**: 학생은 자신이 신청한 이번 학기 강좌 목록(시간표)을 조회할 수 있다.

#### B. 강좌 (Course)
*   **강좌 목록 조회**:
    *   전체 강좌 목록 조회
    *   학과별 강좌 필터링 조회
    *   각 강좌 정보에는 **강좌명, 교수명, 학점, 강의 시간, 전체 정원, 현재 신청 인원**이 포함되어야 한다.

#### C. 교수 (Professor)
*   **교수 목록 조회**: 등록된 교수진 정보를 조회할 수 있어야 한다.

#### D. 수강신청 (Registration)
*   **수강신청**: 학생은 원하는 강좌를 신청할 수 있다.
*   **수강취소**: 학생은 신청했던 강좌를 취소할 수 있다.

### 2.2 비기능적 요구사항 (Non-Functional Requirements)
*   **동시성 제어 (Concurrency Control)**:
    *   정원이 1명 남은 상황에서 100명이 동시에 신청하더라도 **정확히 1명만 성공**해야 한다.
    *   **Race Condition**으로 인한 정원 초과(Overbooking)가 절대 발생해서는 안 된다.
*   **데이터 무결성**: 신청 성공 시 데이터베이스에 정확히 반영되어야 한다.

### 2.3 설계 고민과 이유
*   **동시성 제어 전략**:
    *   정원 초과 방지는 최우선 목표이므로, 처리량보다 **정합성**을 우선한다.
    *   다중 동시 요청 상황에서 오버부킹을 막기 위해 **DB 락(비관적 락)과 분산 락**을 조합하는 전략을 검토한다.
    *   트래픽 급증 상황에서도 “정확히 1명만 성공”을 보장하는 것이 핵심이다.
*   **시간표 충돌 검증**:
    *   “1분이라도 겹치면 충돌” 규칙을 명확히 하기 위해 **반개구간 [start, end)** 비교를 사용한다.
    *   종료 시간과 다음 수업 시작 시간이 같은 경우는 중복이 아니므로, 경계 시간은 허용한다.
    *   강좌 시간은 요일 + 시작/종료 시간으로 모델링해 다중 시간대를 지원한다.
*   **학점 제한 검증**:
    *   신청 시점에 학생의 **현재 신청 학점 합산**을 확인해 18학점 초과를 차단한다.
    *   동시 신청 상황에서도 학점 합산이 정확히 반영되도록 **원자성 있는 처리**가 필요하다.

### 2.4 비즈니스 로직 및 제약사항 (Constraints)
1.  **최대 학점 제한**: 학생당 한 학기에 최대 **18학점**까지만 신청 가능하다.
2.  **시간 중복 불가**: 이미 신청한 강좌와 강의 시간이 겹치는 강좌는 신청할 수 없다.
3.  **정원 제한**: 현재 수강 인원이 정원에 도달한 강좌는 신청할 수 없다.
4.  **중복 신청 불가**: 이미 신청한 강좌를 다시 신청할 수 없다.

## 3. API 명세 (Draft)

| Method | URI | Description | Note |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/students` | 학생 목록 조회 | |
| `GET` | `/api/courses` | 강좌 목록 조회 | 필터링(학과) 가능 |
| `GET` | `/api/professors` | 교수 목록 조회 | |
| `POST` | `/api/enrollment` | 수강신청 | Body: `{studentId, courseId}` |
| `DELETE` | `/api/enrollment` | 수강취소 | Body: `{studentId, courseId}` |
| `GET` | `/api/students/{studentId}/timetable` | 내 시간표 조회 | |

## 4. 데이터 모델 (ERD Draft)

*   **Student**: `id`, `name`, `student_number`, `department`, `total_credits`
*   **Professor**: `id`, `name`, `department`
*   **Course**: `id`, `name`, `professor_id`, `credits`, `capacity`, `current_count`, `schedule` (Day/Time)
*   **Enrollment**: `id`, `student_id`, `course_id`, `created_at`

## 5. 기술 스택 (권장)
*   **Language**: Kotlin
*   **Framework**: Spring Boot
*   **Database**: MySQL, Redis(대기열 혹은 분산 락)
*   **Infra**: Docker
*   **Concurrency**: Pessimistic Lock (비관적 락), Ready Queue(Redis)

---
