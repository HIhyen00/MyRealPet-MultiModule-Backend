# MyRealPet 멀티모듈 백엔드

## 📖 프로젝트 소개

MyRealPet은 반려동물 관리를 위한 종합 플랫폼의 백엔드 서비스입니다. Spring Boot 기반의 멀티모듈 아키텍처로 구성되어 있으며, 각 기능별로 독립적인 모듈과 데이터베이스를 사용하여 마이크로서비스 아키텍처의 장점을 활용합니다.

### 주요 기능

- **계정 관리**: 회원가입, 로그인, 카카오 OAuth 인증
- **반려동물 프로필**: 반려동물 정보 관리, 사진 업로드
- **건강 관리**: 예방접종 기록, 진료 기록, 체중 관리, 생리 주기 추적
- **산책 기록**: 산책 경로 저장, 카카오 지도 연동
- **소셜 기능**: SNS 게시글, 사진 공유
- **Q&A**: 반려동물 관련 질문과 답변
- **AI 기능**: OpenAI를 활용한 건강 리포트 분석

## 📁 프로젝트 구조

```
MyRealPet-MultiModule-Backend/
├── account/              # 계정 및 인증 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8005)
│   ├── client/          # 외부 모듈 연동 인터페이스
│   ├── core/            # 도메인 모델, 서비스, 레포지토리
│   └── dto/             # 데이터 전송 객체
│
├── pet-walk/            # 산책 및 지도 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8002)
│   ├── client/          # 외부 모듈 연동 인터페이스
│   ├── core/            # 도메인 모델, 서비스, 레포지토리
│   └── dto/             # 데이터 전송 객체
│
├── pet-life-cycle/      # 반려동물 생애주기 관리 서비스
│   ├── adminApi/        # 관리자 API (포트 8100)
│   ├── api/             # 사용자 API (포트 8003)
│   ├── client/          # 외부 모듈 연동 인터페이스
│   ├── core/            # 도메인 모델, 서비스, 레포지토리
│   └── dto/             # 데이터 전송 객체
│
├── qna/                 # Q&A 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8004)
│   ├── client/          # 외부 모듈 연동 인터페이스
│   ├── core/            # 도메인 모델, 서비스, 레포지토리
│   └── dto/             # 데이터 전송 객체
│
├── sns/                 # 소셜 네트워크 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8007)
│   ├── client/          # 외부 모듈 연동 인터페이스
│   ├── core/            # 도메인 모델, 서비스, 레포지토리
│   └── dto/             # 데이터 전송 객체
│
├── common/              # 공통 컴포넌트
│   ├── interceptor/     # 인증 인터셉터
│   ├── session/         # Redis 세션 관리
│   └── config/          # 공통 설정
│
├── docker-compose.yml   # Docker Compose 설정
├── Dockerfile           # Docker 이미지 빌드 설정
├── .env                 # 환경 변수 (Git에 포함되지 않음)
└── build.gradle         # Gradle 빌드 설정
```

### 모듈별 역할

| 모듈 | 설명 | 포트 | 데이터베이스 |
|------|------|------|--------------|
| **account** | 회원가입, 로그인, 카카오 OAuth 인증 | 8005 | myrealpet_account |
| **pet-walk** | 산책 경로 기록, 카카오 지도 API 연동 | 8002 | myrealpet_walk |
| **pet-life-cycle** | 반려동물 프로필, 건강 기록, AI 분석 | 8003 | myrealpet_lifecycle |
| **pet-life-cycle-admin** | 품종, 백신 정보 관리 (관리자용) | 8100 | myrealpet_lifecycle |
| **qna** | 질문과 답변, AI 답변 생성 | 8004 | myrealpet_qna |
| **sns** | 게시글, 사진 공유, 좋아요, 댓글 | 8007 | myrealpet_sns |

---

## 🔧 기술 스택

### Backend Framework
- **Spring Boot 3.5.6**: 최신 Spring Boot 프레임워크
- **Spring Data JPA**: ORM 및 데이터 접근 계층
- **Spring Security**: 인증 및 보안 (OAuth2 지원)
- **Gradle**: 빌드 도구 및 멀티모듈 관리

### Database & Cache
- **MySQL 8.0**: 각 모듈별 독립된 데이터베이스
- **Redis**: 세션 관리 및 캐싱
- **Hibernate**: JPA 구현체

### Cloud & Storage
- **AWS S3**: 이미지 및 파일 저장
- **Docker & Docker Compose**: 컨테이너화 및 배포

### External APIs
- **Kakao OAuth**: 소셜 로그인
- **Kakao Maps API**: 지도 및 장소 검색
- **OpenAI API**: AI 기반 건강 분석 및 답변 생성

### Development Tools
- **Lombok**: 보일러플레이트 코드 감소
- **WebClient**: 비동기 HTTP 클라이언트

---

## 🔐 인증 시스템

**UUID 기반 세션 토큰 인증을 사용합니다**:

1. 로그인 시 UUID 토큰 생성
2. Redis에 사용자 세션 저장 (userId와 토큰 매핑)
3. API 요청 시 다음 헤더 필요:
   - `Authorization: Bearer {token}`
   - `X-User-ID: {userId}`

### 인증이 필요하지 않은 API

- `/api/auth/**` - 로그인, 회원가입, 카카오 로그인
- `/api/kakao-maps/**` - 카카오 지도 API (공개)
- `/actuator/**` - 헬스체크

### 인증 흐름

```
1. 사용자 로그인 → POST /api/auth/login
2. 서버에서 UUID 토큰 생성
3. Redis에 토큰 저장 (key: token, value: userId)
4. 클라이언트가 토큰 저장
5. 이후 API 요청 시 헤더에 토큰 포함
6. AuthInterceptor가 토큰 검증 및 userId 추출
```
---

## 🛠️ 개발 가이드

### 멀티모듈 아키텍처

각 모듈은 다음 서브모듈로 구성됩니다:

1. **dto**: 데이터 전송 객체 (Request/Response)
2. **core**: 도메인 모델, 엔티티, 레포지토리, 서비스 인터페이스
3. **client**: 외부 모듈에서 사용할 수 있는 클라이언트 인터페이스
4. **api**: REST API 컨트롤러, Spring Boot 애플리케이션

### 새로운 기능 추가

1. **DTO 정의**: `{module}/dto/` 에 요청/응답 객체 생성
2. **도메인 모델**: `{module}/core/` 에 엔티티 및 레포지토리 생성
3. **비즈니스 로직**: `{module}/core/` 에 서비스 인터페이스 및 구현 생성
4. **API**: `{module}/api/` 에 컨트롤러 생성

### 데이터베이스 전략

- **Database per Service**: 각 모듈이 독립된 데이터베이스 사용
- **DDL 자동 생성**: `hibernate.ddl-auto=update`로 자동 관리
- **데이터베이스별 역할**:
  - `myrealpet_account`: 사용자, 인증 정보
  - `myrealpet_walk`: 산책 경로, 위치 정보
  - `myrealpet_lifecycle`: 반려동물 프로필, 건강 기록
  - `myrealpet_qna`: 질문, 답변
  - `myrealpet_sns`: 게시글, 댓글, 좋아요

### 환경별 설정

- **dev**: 개발 환경 (기본)
- 프로파일별 설정은 `application-{profile}.yml`에 정의

### 빌드 및 배포

```bash
# 전체 프로젝트 빌드
./gradlew clean build

# 특정 모듈 빌드
./gradlew :account:api:build

# JAR 파일 실행
java -jar account/api/build/libs/account-api.jar

# Docker 이미지 빌드
docker build --build-arg SERVICE_NAME=account -t myrealpet-account .

# Docker Compose로 전체 배포
docker-compose up -d
```


## 📞 프론트엔드 연동

프론트엔드에서는 Vite 프록시 설정을 사용:

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      '/api/auth': {
        target: 'http://localhost:8005',
        changeOrigin: true,
      },
      '/api/kakao-maps': {
        target: 'http://localhost:8002',
        changeOrigin: true,
      },
      '/api/pets': {
        target: 'http://localhost:8003',
        changeOrigin: true,
      },
      '/api/admin': {
        target: 'http://localhost:8100',
        changeOrigin: true,
      },
    }
  }
})
```

---

## 🏗️ 아키텍처 상세

### 계층형 아키텍처

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│     (api - Controllers, REST)       │
├─────────────────────────────────────┤
│         Application Layer           │
│    (core - Services, Use Cases)     │
├─────────────────────────────────────┤
│          Domain Layer               │
│   (core - Entities, Repositories)   │
├─────────────────────────────────────┤
│       Infrastructure Layer          │
│    (JPA, Redis, S3, External API)   │
└─────────────────────────────────────┘
```

### 모듈 간 통신

- 각 모듈은 독립적으로 실행 가능
- `client` 모듈을 통해 다른 모듈의 기능 호출 가능
- `common` 모듈이 인증 및 세션 관리 담당

### 인증 흐름

```
Client → API Request → AuthInterceptor → Redis Session Check → Controller
                              ↓ (if authenticated)
                         Add userId to Request
```

---
