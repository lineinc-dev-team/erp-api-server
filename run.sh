#!/bin/bash

ENV=$1

if [ -z "$ENV" ]; then
  echo "사용법: ./run.sh [local|dev|prod]"
  exit 1
fi

case "$ENV" in
  local)
    echo "🔧 로컬 환경 실행 중..."
    docker-compose up --build
    ;;

  dev)
    echo "🌐 개발 환경 실행 중..."
    docker-compose -f docker-compose.yaml -f docker-compose.dev.yaml up --build
    ;;

  prod)
    echo "🚀 운영 환경 실행 중..."
    docker-compose -f docker-compose.yaml -f docker-compose.prod.yaml up --build
    ;;

  *)
    echo "❌ 알 수 없는 환경: $ENV"
    echo "사용법: ./run.sh [local|dev|prod]"
    exit 1
    ;;
esac