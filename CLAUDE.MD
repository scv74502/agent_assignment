# CLAUDE.md

## 기록 관리
**모든 AI Agent를 통한 작업 내역을 **/Users/kwp/Desktop/stress-test/mss_test/prompts/history.md**에 기록할 것**
- 시간순, 작업 순으로 관리
- 에이전트한테 보낸 요청과 작업 내용 요약을 모두 명시
### 허용되는 형식
```markdown
- 프롬프트를 직접 기록한 마크다운 파일
- AI 에이전트의 대화 내보내기
- 자동화된 로깅
```

## 정보
- kotlin + mysql + redis 활용한 수강신청 관련 API
- ktlint를 통해 코드의 품질을 관리
- 변수명과 클래스명은 Camel case를 따르며 네이밍 일관성을 확보해야 함
- .env + dovenv를 통해 설정값 관리

## 코틀린 규칙
- value object, inline function, sealed class 사용
- error는 가능한 한 global handler 두어서 처리
- error의 종류에 따라서 다른 종류 handler 사용
- SRP 준수를 위해 가능한 하나의 클래스-인터페이스는 하나의 파일에

## Spring 규칙
- api 엔드포인트와 openapi 문서를 인터페이스로 미리 작성하고, 이를 상속받아 실제 api 엔드포인트를 구현 
- 레이어드 아키텍처를 사용하되 관심사에 따라 디렉토리를 분리하고, 관심사 간 디렉토리 구조 일관성은 유지
- 테스트를 통해 코드를 검증하고, 코드의 비즈니스 로직과 테스트를 항상 일치시킴
- 단위 테스트/통합 테스트를 모두 구성하고 통합 테스트는 별도 테스트용 인프라에서 수행
- org/example/msstest/controller/openapi에서 인터페이스로 swagger 먼저 구현하고 org/example/msstest/controller에서 상속받아 엔드포인트 구현

## 인프라 규칙
- spring 제외한 redis, mysql 모두 별도 docker 컨테이너 통해 실행ㄴ
- 통합 테스트의 경우 TestContainer 활용하여 인프라 구성 후 통합 테스트

## 커밋 규칙

- **Github Flow 따름**
- 형식: `[type] 제목`
- 본문(body), footer, Co-Authored-By 트레일러를 포함하지 않는다
- type 종류: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`
- 제목은 한글로 간결하게 작성한다
- 공동 작업자에 Agent 제외하고 사용자만 명시

### 예시

```
[feat] 회원 조회 및 삭제 API 추가
[fix] Logback JSON_CONSOLE 설정 수정
[refactor] ExceptionLogger ANSI 코드 제거
[test] Storage 레이어 통합 테스트 보강
```

## 브랜치 규칙

- 네이밍: `feature/<이슈번호>-<간단한설명>`
- 예시: `feature/1-multi-module-setup`
- main 브랜치에 직접 push하지 않는다

## Active Technologies
- Kotlin 1.9.25 + Java 21 + Spring Boot 3.5.10, Spring Data JPA, Spring Security + JWT, SpringDoc OpenAPI (001-course-registration)
- MySQL (영속 데이터), Redis (분산 락 + 대기열) (001-course-registration)

## Recent Changes
- 001-course-registration: Added Kotlin 1.9.25 + Java 21 + Spring Boot 3.5.10, Spring Data JPA, Spring Security + JWT, SpringDoc OpenAPI
