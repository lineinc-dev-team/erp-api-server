#!/bin/bash
set -euo pipefail

# =======================================
# 🚀 무중단 배포 스크립트 (Blue-Green)
# =======================================

PROJECT_ROOT="/home/ubuntu"
JAR_SOURCE="$PROJECT_ROOT/app-new.jar"
JAR_TARGET="$PROJECT_ROOT/app-latest.jar"

HEALTH_CHECK_TIMEOUT=60
HEALTH_CHECK_INTERVAL=3
MAX_RETRIES=$((HEALTH_CHECK_TIMEOUT / HEALTH_CHECK_INTERVAL))

# 배포 파일 준비
prepare_deployment() {
    echo "📂 [1/5] 배포 파일 준비..."
    if [ ! -f "$JAR_SOURCE" ]; then
        echo "❌ 오류: $JAR_SOURCE 파일이 없습니다."
        exit 1
    fi
    mv -f "$JAR_SOURCE" "$JAR_TARGET"
}

# 포트 및 앱 이름 결정
determine_ports() {
    echo "🔍 [2/5] 현재 실행 중인 포트 확인..."

    if ss -tlnp | grep -q ":8080[[:space:]]"; then
        NEW_PORT=8081
        OLD_PORT=8080
    else
        NEW_PORT=8080
        OLD_PORT=8081
    fi

    NEW_APP="app-${NEW_PORT}"
    OLD_APP="app-${OLD_PORT}"

    echo "👉 새 포트: $NEW_PORT | 기존 포트: $OLD_PORT"
}

# 새 애플리케이션 시작
start_new_app() {
    echo "🚀 [3/5] 새 앱($NEW_APP) 시작..."

    if pm2 list | grep -q "$NEW_APP"; then
        pm2 restart "$NEW_APP"
    else
        pm2 start ecosystem.config.js --only "$NEW_APP"
    fi
}

# 헬스 체크 헬퍼 함수
wait_for_condition() {
    local check_command=$1
    local success_msg=$2
    local error_msg=$3

    for i in $(seq 1 $MAX_RETRIES); do
        if eval "$check_command"; then
            echo "   ✅ $success_msg"
            return 0
        fi
        sleep $HEALTH_CHECK_INTERVAL
    done

    echo "   ❌ $error_msg"
    show_logs_and_exit
}

# 헬스 체크
check_health() {
    echo "🏥 [4/5] 헬스 체크 시작..."

    wait_for_condition \
        "ss -tlnp | grep -q ':$NEW_PORT[[:space:]]'" \
        "포트 $NEW_PORT 연결 성공" \
        "포트 오픈 실패"

    wait_for_condition \
        "curl -sf --max-time 5 http://localhost:${NEW_PORT}/actuator/health | grep -q '\"status\":\"UP\"'" \
        "서비스 상태: UP" \
        "헬스 체크 실패"
}

# 로그 출력 및 종료
show_logs_and_exit() {
    echo "🔍 최근 로그:"
    pm2 logs "$NEW_APP" --lines 30 --nostream
    exit 1
}

# 기존 앱 정리
cleanup_old_app() {
    echo "🧹 [5/5] 기존 앱($OLD_APP) 정리..."

    # Nginx가 새 포트로 트래픽을 전환할 시간 확보
    echo "   ⏳ 트래픽 전환 대기 중... (3초)"
    sleep 3

    pm2 delete "$OLD_APP" 2>/dev/null || true
    pm2 save --force
    echo "🎉 배포 완료!"
}

# 메인 실행
prepare_deployment
determine_ports
start_new_app
check_health
cleanup_old_app
