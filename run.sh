#!/bin/bash
# 실행 시 사용할 환경(local/dev/prod)을 첫 번째 인자로 받음
ENV=$1

# 인자가 비어있으면 사용법을 출력하고 종료
if [ -z "$ENV" ]; then
  echo "사용법: ./run.sh [local|dev|prod]"
  exit 1
fi

# 받은 환경값에 따라 실행 방식 분기
case "$ENV" in
  local)
    echo "🔧 로컬 환경 실행 중..."
    # 기본 docker-compose.yml만 사용하여 로컬 환경 실행
    docker-compose up --build
    ;;

  dev)
    echo "🌐 개발 환경 실행 중..."
    # 기본 + 개발 환경 오버라이드 파일 조합 실행
    docker-compose -f docker-compose.yaml -f docker-compose.dev.yaml up --build
    ;;

  prod)
    echo "🚀 운영 환경 실행 중..."
    # 기본 + 운영 환경 오버라이드 파일 조합 실행
    docker-compose -f docker-compose.yaml -f docker-compose.prod.yaml up --build
    ;;

  *)
    # 인자가 잘못된 경우 에러 메시지 출력
    echo "❌ 알 수 없는 환경: $ENV"
    echo "사용법: ./run.sh [local|dev|prod]"
    exit 1
    ;;
esac