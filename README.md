## 🔧 서버 실행 방법

서버를 실행할 때는 `run.sh` 스크립트를 사용해 주세요.  
환경(`local`, `dev`, `prod`)을 인자로 넣어 실행할 수 있습니다.

```bash
./run.sh [local|dev|prod]
```

### ✅ 사전 준비

- 실행 전에 .env 파일에 환경변수를 반드시 설정해 주세요.
- GitHub 등에서 run.sh 스크립트를 처음 받아오면 실행 권한을 부여해야 합니다.

```bash
chmod +x run.sh
```

## 🧠 프로젝트 구조 (Domain-Driven Design 기반)

본 프로젝트는 도메인 주도 설계(DDD)를 철저히 반영하여, 도메인 중심으로 책임과 관심사를 분리한 구조를 가지고 있습니다.
기능(기술 스택)별 구분보다는 비즈니스 도메인별로 묶어, 각 도메인이 독립적으로 관리될 수 있도록 하며, 변경에 유연하게 대응하도록 설계하였습니다.

```bash
src/main/java/com.lineinc.erp.api.server
├── domain/           # 핵심 도메인 모델 및 비즈니스 로직
│   ├── company/      # 회사 관련 Entity, Value Object, Repository 등
│   ├── user/         # 사용자 관련 도메인 모델 및 리포지토리
│   └── auth/         # 인증/인가 관련 도메인 (예: RefreshToken 등)
│
├── application/      # 유스케이스, 서비스, DTO 등 (비즈니스 로직과 외부 데이터 교환 담당)
│   ├── company/      # 회사 도메인 관련 비즈니스 로직 구현
│   ├── user/         # 사용자 관련 서비스 구현
│   └── auth/         # 인증 서비스 구현
│
├── interface/        # 외부 요청과의 접점 (웹, API, 보안, 이벤트 등)
│   ├── user/         # REST API Controller 등
│   ├── auth/         # 인증 관련 API 컨트롤러 및 필터
│   └── jwt/          # JWT 관련 구현 (필터, 유틸 등)
│
├── infrastructure/   # 외부 시스템 연동 및 기술 인프라 구현
│   ├── persistence/  # DB 연동 구현체, JPA, MyBatis 등
│   └── security/     # 보안 관련 인프라 (ex. JWT 토큰 생성 유틸리티)
│
├── config/           # 애플리케이션 전역 설정 (보안, Swagger, CORS 등)
│
├── exception/        # 커스텀 예외 클래스 및 글로벌 예외 처리
│
└── ErpApiServerApplication.java   # 메인 애플리케이션 실행 진입점
```

