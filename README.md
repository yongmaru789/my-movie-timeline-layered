# my_movie_timeline - Layered Architecture

기존 [my_movie_timeline](https://github.com/yongmaru789/my_movie_timeline) 프로젝트를
**패키지 계층 구조(Package by Layer)** 기반으로 재작성한 학습용 프로젝트입니다.

동일한 기능을 두 가지 아키텍처로 구현해보면서 각 방식의 차이점을 직접 경험하고,
Spring Boot 백엔드 개발의 핵심 패턴들을 학습하는 것을 목적으로 합니다.

---

## 새로운 리포지토리를 만든 이유

기존 프로젝트는 **기능별 구조(Package by Feature)** 로 설계되어 있었습니다.
```
com.mymovie.backend
├── movie/
├── user/
├── recommend/
├── jwt/
└── security/
```


이번 프로젝트에서는 Spring MVC 계층 구조를 명시적으로 학습하기 위해
**계층별 구조(Package by Layer)** 로 재구성했습니다.
```
com.example.movie
├── controller/
├── service/
│   └── impl/
├── repository/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── exception/
├── jwt/
├── security/
└── common/
```


기존 프로젝트를 수정하지 않고 새로운 리포지토리를 만든 이유는,
두 구조를 나란히 비교하면서 차이점을 명확하게 이해하기 위해서입니다.

---

## 기존 프로젝트 대비 개선 사항

### DTO 분리 (Request / Response)
엔티티를 요청/응답에 직접 사용하던 방식에서 벗어나
`MovieRequestDto`, `MovieResponseDto`를 분리해 민감한 정보 노출을 방지하고
클라이언트와 서버 간의 데이터 계약을 명확히 했습니다.

### Service 인터페이스 + 구현체 분리
`MovieService` 인터페이스와 `MovieServiceImpl` 구현체를 분리해
Controller가 구현 방식에 의존하지 않는 구조를 적용했습니다.

### 테스트 코드 추가
기존 프로젝트에 없던 테스트 코드를 추가했습니다.
Lombok 적용, 공통 예외 처리 분리 등 코드 품질 개선도 함께 진행했습니다.

| 테스트 종류 | 파일 | 도구 |
|------------|------|------|
| 단위 테스트 | `MovieServiceTest.java` | JUnit5, Mockito |
| 통합 테스트 | `MovieControllerTest.java` | JUnit5, MockMvc |

---

## 프로젝트 구조
```
src/main/java/com/example/movie/
├── MovieApplication.java
├── common/
│   ├── ApiResponse.java
│   ├── CorsConfig.java
│   └── GlobalExceptionHandler.java
├── controller/
│   ├── AuthController.java
│   ├── HealthController.java
│   └── MovieController.java
├── dto/
│   ├── request/
│   │   ├── MovieRequestDto.java
│   │   └── UserRequestDto.java
│   └── response/
│       ├── MovieResponseDto.java
│       └── UserResponseDto.java
├── entity/
│   ├── Movie.java
│   └── User.java
├── exception/
│   └── MovieNotFoundException.java
├── jwt/
│   ├── JwtFilter.java
│   └── JwtUtil.java
├── repository/
│   ├── MovieRepository.java
│   └── UserRepository.java
├── security/
│   └── SecurityConfig.java
└── service/
    ├── MovieService.java
    ├── UserService.java
    └── impl/
        ├── MovieServiceImpl.java
        └── UserServiceImpl.java
```

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Build Tool | Gradle |
| Database | MySQL |
| ORM | Spring Data JPA |
| Security | Spring Security, JWT |
| Test | JUnit5, Mockito, MockMvc |
| 기타 | Lombok |

---

## 관련 프로젝트

기존 프로젝트 (기능별 구조) → [my_movie_timeline](https://github.com/yongmaru789/my_movie_timeline)
