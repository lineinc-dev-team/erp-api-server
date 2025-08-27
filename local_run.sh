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

echo "⚙️  Spring Boot 애플리케이션 실행 중..."
./gradlew bootRun