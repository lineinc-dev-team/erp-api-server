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

본 프로젝트는 도메인 주도 설계(DDD)를 기반으로 계층 간 관심사를 분리하여 설계되었습니다.
기능 중심이 아닌 **도메인 중심 구조**로, 유지보수성과 확장성, 테스트 용이성을 높였습니다.

```bash
src/main/java/com/lineinc/erp/api/server
├── domain/             # 비즈니스 핵심 도메인 모델
│   ├── company/        # 회사 관련 Entity, Enum, Repository
│   └── users/          # 사용자 관련 Entity, Enum, Repository
│
├── application/        # 도메인 서비스 및 유스케이스 구현
│   └── auth/           # 인증 관련 서비스 계층
│
├── presentation/       # 외부 요청을 처리하는 계층 (Controller + DTO)
│   └── auth/
│       ├── controller/ # 인증 관련 API 컨트롤러
│       └── dto/        # 요청/응답 DTO
│
├── common/             # 공통 모듈
│   └── dto/            # 에러 응답 등 범용 DTO
│
├── config/             # 전역 설정 (보안, Swagger, CORS 등)
│
├── exception/          # 전역 예외 처리 및 예외 정의
│
└── ErpApiServerApplication.java  # 애플리케이션 진입점
```
