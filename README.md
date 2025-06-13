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

## 🏗️ 프로젝트 구조

- 본 프로젝트는 계층형 아키텍처 (Layered Architecture) 를 기반으로 구성되었습니다.

```bash
src/main/java/com.lineinc.erp.api.server
├── controller/     # HTTP 요청을 처리하는 웹 계층 (RestController)
├── service/        # 비즈니스 로직을 담당하는 서비스 계층
├── repository/     # 데이터베이스 접근을 위한 JPA/MyBatis 등 리포지토리 계층
├── domain/         # 핵심 도메인 모델 (Entity, Enum 등)
├── dto/            # 계층 간 데이터 전달용 DTO 클래스들
├── config/         # 보안, CORS, Swagger 등 설정 클래스
├── exception/      # 커스텀 예외 및 예외 핸들링 처리
└── ErpApiServerApplication.java  # 메인 실행 클래스
```

