#!/bin/bash
echo "🔧 로컬 환경 실행 중..."

# Docker Compose 실행 및 에러 체크
if ! docker-compose -f docker-compose.local.yaml up --build -d; then
  echo "❌ Docker Compose 실행 실패"
  exit 1
fi

# .env 파일 로드
if [ -f .env ]; then
  export $(cat .env | grep -v '^#' | xargs)
else
  echo "⚠️  .env 파일이 존재하지 않습니다. 서비스를 종료합니다."
  exit 1
fi

# 스크립트 종료(Ctrl+C) 시 백그라운드 작업도 같이 종료
cleanup() {
    echo "🛑 프로세스 종료 중..."
    kill $(jobs -p) 2>/dev/null
}
trap cleanup EXIT

echo "🔥 [1/2] 자동 컴파일러(Hot Reload) 실행 중..."
# 소스 코드가 변경되면 자동으로 컴파일을 수행합니다.
./gradlew compileJava -t &

echo "⚙️  [2/2] Spring Boot 애플리케이션 실행 중..."
# DevTools가 컴파일된 파일을 감지하여 서버를 재시작합니다.
./gradlew bootRun
