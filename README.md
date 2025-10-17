# MyRealPet 멀티모듈 백엔드

## 🚀 빠른 시작

### 1. 환경 설정

프로젝트 루트에 `.env` 파일을 생성하세요:

```bash
# 데이터베이스 설정
ACCOUNT_DB_URL=jdbc:mysql://localhost:3306/myrealpet_account?allowpublickeyretrieval=true&usessl=false&serverTimezone=UTC
ACCOUNT_DB_USERNAME=myrealpet
ACCOUNT_DB_PASSWORD=roota123

PETWALK_DB_URL=jdbc:mysql://localhost:3306/myrealpet_walk?allowpublickeyretrieval=true&usessl=false&serverTimezone=UTC
PETWALK_DB_USERNAME=myrealpet
PETWALK_DB_PASSWORD=roota123

PETLIFECYCLE_DB_URL=jdbc:mysql://localhost:3306/myrealpet_lifecycle?allowpublickeyretrieval=true&usessl=false&serverTimezone=UTC
PETLIFECYCLE_DB_USERNAME=myrealpet
PETLIFECYCLE_DB_PASSWORD=roota123

QNA_DB_URL=jdbc:mysql://localhost:3306/myrealpet_qna?allowpublickeyretrieval=true&usessl=false&serverTimezone=UTC
QNA_DB_USERNAME=myrealpet
QNA_DB_PASSWORD=roota123

SNS_DB_URL=jdbc:mysql://localhost:3306/myrealpet_qna?allowpublickeyretrieval=true&usessl=false&serverTimezone=UTC
SNS_DB_USERNAME=myrealpet
SNS_DB_PASSWORD=roota123

# Redis 설정 (세션 관리용)
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=eddi@123

# 카카오 API 설정
KAKAO_API_KEY=your-kakao-rest-api-key
KAKAO_CLIENT_ID=your-kakao-client-id

# 서버 포트 설정
ACCOUNT_PORT=8005
PETWALK_PORT=8002
PETLIFECYCLE_PORT=8003
PETADMIN_PORT=8100
QNA_PORT=8004
SNS_PORT=8007

# 기타 설정
SPRING_PROFILES_ACTIVE=dev
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
LOG_LEVEL=DEBUG
```

### 2. 데이터베이스 설정

MySQL에서 두 개의 데이터베이스를 생성하세요:

```sql
CREATE DATABASE myrealpet_account;
CREATE DATABASE myrealpet_walk;
CREATE DATABASE myrealpet_lifecycle;
CREATE DATABASE myrealpet_qna;
CREATE DATABASE myrealpet_sns;
CREATE USER 'myrealpet'@'localhost' IDENTIFIED BY 'roota123';
GRANT ALL PRIVILEGES ON myrealpet_account.* TO 'myrealpet'@'localhost';
GRANT ALL PRIVILEGES ON myrealpet_walk.* TO 'myrealpet'@'localhost';
GRANT ALL PRIVILEGES ON myrealpet_lifecycle.* TO 'myrealpet'@'localhost';
GRANT ALL PRIVILEGES ON myrealpet_qna.* TO 'myrealpet'@'localhost';
GRANT ALL PRIVILEGES ON myrealpet_sns.* TO 'myrealpet'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Redis 설치 및 시작

Redis가 설치되어 있어야 합니다:
- Windows: https://github.com/microsoftarchive/redis/releases
- macOS: `brew install redis && brew services start redis`
- Linux: `sudo apt install redis-server && sudo systemctl start redis`

### 4. 서버 실행

각 서비스를 독립적으로 실행:

```bash
# Account 서비스 (포트 8005)
./gradlew :account:api:bootRun

# Pet-Walk 서비스 (포트 8002)
./gradlew :pet-walk:api:bootRun
```

## 📁 프로젝트 구조

```
MyRealPet-MultiModule-Backend/
├── account/              # 계정 및 인증 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8005)
│   ├── client/          # 비즈니스 로직 구현
│   ├── core/            # 도메인 모델 및 레포지토리
│   └── dto/             # 데이터 전송 객체
├── pet-walk/            # 산책 및 지도 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8002)
│   ├── client/          # 비즈니스 로직 구현
│   ├── core/            # 도메인 모델 및 레포지토리
│   └── dto/             # 데이터 전송 객체
├── pet-life-cycle/      # 펫 관리 서비스
│   ├── adminApi/        # REST API 컨트롤러 (포트 8100) 
│   ├── api/             # REST API 컨트롤러 (포트 8003)
│   ├── client/          # 비즈니스 로직 구현
│   ├── core/            # 도메인 모델 및 레포지토리
│   └── dto/             # 데이터 전송 객체
├── qna/                 # QNA 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8004)
│   ├── client/          # 비즈니스 로직 구현
│   ├── core/            # 도메인 모델 및 레포지토리
│   └── dto/             # 데이터 전송 객체
├── sns/                 # SNS 서비스
│   ├── api/             # REST API 컨트롤러 (포트 8007)
│   ├── client/          # 비즈니스 로직 구현
│   ├── core/            # 도메인 모델 및 레포지토리
│   └── dto/             # 데이터 전송 객체
├── common/              # 공통 컴포넌트
│   ├── interceptor/     # 인증 인터셉터
│   └── session/         # Redis 세션 관리
└── .env                 # 환경 변수 (Git에 포함되지 않음)
```

## 🔧 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Database**: MySQL 8.0 (분리된 두 개의 DB)
- **Cache/Session**: Redis
- **Build Tool**: Gradle
- **Authentication**: 간단한 UUID 토큰 + Redis 세션
- **External API**: Kakao Maps API

## 🔐 인증 시스템

**간단한 UUID 기반 세션 인증을 사용합니다** (JWT 제거됨):

1. 로그인 시 UUID 토큰 생성
2. Redis에 사용자 세션 저장
3. API 요청 시 `Authorization: Bearer {token}` 헤더와 `X-User-ID: {userId}` 헤더 필요

### 인증이 필요하지 않은 API

- `/api/auth/**` - 로그인, 회원가입, 카카오 로그인
- `/api/kakao-maps/**` - 카카오 지도 API (공개)
- `/actuator/**` - 헬스체크

## 📝 API 엔드포인트

### Account Service (포트 8005)

```
POST /api/auth/register     # 회원가입
POST /api/auth/login        # 로그인
POST /api/auth/logout       # 로그아웃
POST /api/auth/kakao/token  # 카카오 토큰 로그인
GET  /api/auth/me           # 현재 사용자 정보
```

### Pet-Walk Service (포트 8002)

```
GET /api/kakao-maps/search  # 카카오 지도 키워드 검색
```

### Pet-Life-Cycle Service (포트 8003)

```

POST   /api/pets/files/upload
DELETE /api/pets/files/{fileId}

GET    /api/pets/breeds/dropdown

POST   /api/pets/profiles
GET    /api/pets/profiles
GET    /api/pets/profiles/{petId}
PUT    /api/pets/profiles/{petId}
DELETE /api/pets/profiles/{petId}

GET    /api/pets/vaccines/{species}

POST   /api/pets/{petId}/vac-records
GET    /api/pets/{petId}/vac-records
GET    /api/pets/{petId}/vac-records/{recordId}
PUT    /api/pets/{petId}/vac-records/{recordId}
DELETE /api/pets/{petId}/vac-records/{recordId}

POST   /api/pets/{petId}/medical-records/analyze-receipt
POST   /api/pets/{petId}/medical-records
GET    /api/pets/{petId}/medical-records
GET    /api/pets/{petId}/medical-records/{recordId}
PUT    /api/pets/{petId}/medical-records/{recordId}
DELETE /api/pets/{petId}/medical-records/{recordId}

POST   /api/pets/{petId}/weights
GET    /api/pets/{petId}/weights

POST   /api/pets/{petId}/cycles
GET    /api/pets/{petId}/cycles 
DELETE /api/pets/{petId}/cycles/{cycleId}

POST   /api/pets/{petId}/health-notes
GET    /api/pets/{petId}/health-notes

POST   /api/pets/{petId}/health-reports
POST   /api/pets/{petId}/health-reports/{reportId}/ask-ai

GET    /api/users/missions
GET    /api/users/{userId}/missions/history
POST   /api/users/{userId}/missions/{missionId}/completions      
DELETE /api/users/{userId}/missions/{missionId}/completions

POST   /api/pets/{petId}/daily-missions
GET    /api/pets/{petId}/daily-missions
PUT    /api/pets/{missionId}/complete
```

### Pet-Admin Service (포트 8100)

```
POST   /api/admin/pets/vaccines
GET    /api/admin/pets/vaccines
GET    /api/admin/pets/vaccines/{vaccineId}
PUT    /api/admin/pets/vaccines/{vaccineId}
DELETE /api/admin/pets/vaccines/{vaccineId}

POST   /api/admin/pets/breeds
GET    /api/admin/pets/breeds
GET    /api/admin/pets/breeds/{breedId}
PUT    /api/admin/pets/breeds/{breedId}
DELETE /api/admin/pets/breeds/{breedId}
```

### QnA Service (포트 8004)

```

```

### SNS Service (포트 8007)

```

```

## 🛠️ 개발 가이드

### 새로운 기능 추가

1. **DTO 정의**: `{module}/dto/` 에 요청/응답 객체 생성
2. **도메인 모델**: `{module}/core/` 에 엔티티 및 레포지토리 생성
3. **비즈니스 로직**: `{module}/client/` 에 서비스 구현
4. **API**: `{module}/api/` 에 컨트롤러 생성

### 데이터베이스 마이그레이션

- **Account DB**: 사용자, 인증 관련 테이블
- **Pet-Walk DB**: 산책, 지도 관련 테이블
- DDL은 `hibernate.ddl-auto=update`로 자동 관리

### 환경별 설정

- **dev**: 개발 환경 (기본)
- 프로파일별 설정은 `application-{profile}.yml`에 정의

## 🚨 주의사항

1. **환경 변수**: `.env` 파일은 Git에 커밋하지 마세요
2. **포트 충돌**: Account(8005), Pet-Walk(8002) 포트가 사용 중이지 않은지 확인
3. **Redis 연결**: Redis 서버가 실행 중인지 확인
4. **데이터베이스 권한**: MySQL 사용자 권한이 올바르게 설정되었는지 확인

## 🔍 트러블슈팅

### 환경 변수 로딩 실패
```
PetWalk DB_URL from System.getProperty(): null
```
→ `.env` 파일이 프로젝트 루트에 있는지 확인

### 포트 충돌
```
Port 8002 was already in use
```
→ `netstat -ano | findstr :8002`로 프로세스 확인 후 종료

### Redis 연결 실패
```
Unable to connect to Redis
```
→ Redis 서버 상태 확인: `redis-cli ping`

### 인증 실패
```
Missing X-User-ID header
```
→ API 요청 시 `Authorization`과 `X-User-ID` 헤더 모두 포함

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
    }
  }
})
```
ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
## 🤖 프로젝트 구조 심층 분석 (AI 분석)

이 섹션은 AI가 프로젝트 코드를 직접 분석하여 작성한 내용입니다.

### 주요 아키텍처

- **Gradle 멀티모듈**: 기능 단위(계정, 산책)로 모듈이 분리되어 독립적인 개발 및 배포가 용이합니다.
- **계층형 아키텍처 (Layered Architecture)**: 각 모듈은 `api`(표현), `core`(도메인), `client`(서비스 구현), `dto`(데이터)로 역할이 명확하게 나뉘어 있습니다.
- **인터페이스 기반 설계**: `core` 모듈에 서비스 인터페이스를 정의하고, `client` 또는 다른 모듈에서 이를 구현하여 모듈 간 결합도를 낮춥니다. (느슨한 결합)
- **중앙 인증 처리**: `common` 모듈의 `AuthInterceptor`가 로그인/회원가입을 제외한 대부분의 API 요청을 가로채 인증을 전담합니다.

### 모듈 상세 분석

#### 1. `common` 모듈: 전역 설정 및 인증 처리
- **`interceptor/AuthInterceptor.java`**: 이 프로젝트의 **인증/인가 핵심**입니다.
    - `/api/auth/**` 등 일부 경로를 제외한 모든 `/api/**` 요청을 가로채 헤더의 `Bearer 토큰`을 검증합니다.
    - 토큰이 유효하면 Redis에서 세션 정보를 조회하여 요청에 `userId`를 담아 컨트롤러로 전달합니다.
    - 즉, **로그인하지 않은 사용자는 대부분의 API를 사용할 수 없습니다.**
- **`config/CommonWebConfig.java`**: `application.yml` 파일의 `app.auth.interceptor.enabled=true` 설정이 있어야만 위 `AuthInterceptor`를 활성화합니다.

#### 2. `account` 모듈: 사용자 인증/계정 관리
- **`api/controller/AccountController.java`**: 로그인, 회원가입, 로그아웃, 카카오 로그인 등 사용자에게 직접 노출되는 API 엔드포인트입니다. 모든 실제 로직은 `AccountService`에 위임합니다.
- **`core/service/AccountService.java`**: `account` 모듈이 제공하는 기능 목록(인터페이스)입니다.
- **`api/config/SecurityConfig.java`**: JWT 대신 세션 토큰을 사용하므로 `STATELESS` 정책을 사용하고, `csrf` 보호는 비활성화합니다.

#### 3. `pet-walk` 모듈: 산책 기능 및 외부 API 연동
- **`api/controller/WalkRouteController.java`**: 산책로 생성/조회/수정/삭제(CRUD) API를 제공합니다. 모든 기능은 `AuthInterceptor`가 요청에 담아준 `userId`를 기반으로 동작하므로, **반드시 로그인이 필요**합니다.
- **`client/service/KakaoMapsServiceImpl.java`**: **카카오 지도 API를 실제로 호출하는 구현체**입니다.
    - `WebClient`를 사용하여 카카오 서버와 비동기 통신합니다.
    - `core` 모듈은 `KakaoMapsService` 인터페이스에만 의존하므로, 나중에 구글 지도로 변경해도 `core` 모듈의 수정 없이 이 파일만 교체하면 됩니다. (SOLID 원칙)

### 사용되지 않는 코드 및 확인 필요 사항

- **`account:core:service:AccountService`의 미사용 메소드**:
    - `AccountController`에서 현재 사용하지 않는 `updatePassword`, `deactivateAccount`, `deleteAccount` 등의 메소드들이 인터페이스에 정의되어 있습니다.
    - 이는 현재 API로는 호출할 수 없으며, 향후 관리자 기능 등으로 사용될 가능성이 있습니다.

- **`account:client` 모듈**:
    - 현재 `account:api` 모듈은 이 모듈을 전혀 사용하지 않고 `account:core`에 직접 의존합니다.
    - 따라서 `account:client`는 **현재 프로젝트에서 사용되지 않는 모듈**입니다. 추후 마이크로서비스 간 통신을 위해 미리 만들어 둔 뼈대 코드로 보입니다.
