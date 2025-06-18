#!/bin/bash

ENV=$1

if [ -z "$ENV" ]; then
  echo "사용법: ./run.sh [local|dev|prod]"
  exit 1
fi

case "$ENV" in
  local)
    echo "🔧 로컬 환경 실행 중 (DB 및 pgAdmin만)..."
    docker-compose -f docker-compose.local.yaml up --build -d
    echo "⚙️  Spring Boot 애플리케이션 실행 중 (bootRun + profile=local)..."
    env SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
    ;;

  dev|prod)
    echo "🚀 $ENV 환경 실행 중..."
    docker-compose -f docker-compose.yaml up --build
    ;;

  *)
    echo "❌ 알 수 없는 환경: $ENV"
    echo "사용법: ./run.sh [local|dev|prod]"
    exit 1
    ;;
esac