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
CREATE USER 'myrealpet'@'localhost' IDENTIFIED BY 'roota123';
GRANT ALL PRIVILEGES ON myrealpet_account.* TO 'myrealpet'@'localhost';
GRANT ALL PRIVILEGES ON myrealpet_walk.* TO 'myrealpet'@'localhost';
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