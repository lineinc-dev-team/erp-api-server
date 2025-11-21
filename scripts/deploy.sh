#!/bin/bash
set -euo pipefail

# =======================================
# 🚀 무중단 배포 스크립트 (Blue-Green)
# =======================================

PROJECT_ROOT="/home/ubuntu"
JAR_SOURCE="$PROJECT_ROOT/app-new.jar"
JAR_TARGET="$PROJECT_ROOT/app-latest.jar"

# 1. 배포 파일 준비
function prepare_deployment() {
    echo "📂 [1/5] 배포 파일 준비..."
    if [ ! -f "$JAR_SOURCE" ]; then
        echo "❌ 오류: $JAR_SOURCE 파일이 없습니다."
        exit 1
    fi
    mv -f "$JAR_SOURCE" "$JAR_TARGET"
}

# 2. 포트 및 앱 이름 결정
function determine_ports() {
    echo "🔍 [2/5] 현재 실행 중인 포트 확인..."

    # 8080 포트가 사용 중이면 -> 8081로 배포 (반대면 8080)
    if ss -tlnp | grep -q ":8080[[:space:]]"; then
        NEW_PORT=8081
        OLD_PORT=8080
    else
        NEW_PORT=8080
        OLD_PORT=8081
    fi

    NEW_APP="app-${NEW_PORT}"
    OLD_APP="app-${OLD_PORT}"

    echo "👉 결정된 포트: $NEW_PORT (새로 배포), $OLD_PORT (기존 중단)"
}

# 3. 새 애플리케이션 시작
function start_new_app() {
    echo "🚀 [3/5] 새 앱($NEW_APP) 시작..."

    if pm2 list | grep -q "$NEW_APP"; then
        echo "   - 기존 프로세스 재시작"
        pm2 restart "$NEW_APP"
    else
        echo "   - 새 프로세스 시작"
        pm2 start ecosystem.config.js --only "$NEW_APP"
    fi
}

# 4. 헬스 체크 (포트 및 서비스 상태)
function check_health() {
    echo "🏥 [4/5] 헬스 체크 시작..."

    # 4-1. 포트 오픈 대기 (최대 60초)
    for i in {1..20}; do
        if ss -tlnp | grep -q ":$NEW_PORT[[:space:]]"; then
            echo "   ✅ 포트 $NEW_PORT 연결 성공"
            break
        fi
        sleep 3
        if [ $i -eq 20 ]; then
            echo "   ❌ 포트 오픈 실패"
            show_logs_and_exit
        fi
    done

    # 4-2. Actuator 상태 확인 (최대 60초)
    for i in {1..20}; do
        response=$(curl -s --max-time 5 "http://localhost:${NEW_PORT}/actuator/health")
        if echo "$response" | grep -q '"status":"UP"'; then
            echo "   ✅ 서비스 상태: UP"
            return 0
        fi
        sleep 3
        if [ $i -eq 20 ]; then
            echo "   ❌ 헬스 체크 실패 (응답: $response)"
            show_logs_and_exit
        fi
    done
}

# 로그 출력 및 종료 헬퍼
function show_logs_and_exit() {
    echo "🔍 최근 로그 확인:"
    pm2 logs "$NEW_APP" --lines 30
    exit 1
}

# 5. 기존 앱 종료 및 정리
function cleanup_old_app() {
    echo "🧹 [5/5] 기존 앱($OLD_APP) 정리..."
    pm2 delete "$OLD_APP" 2>/dev/null || true
    pm2 save --force
    echo "🎉 배포가 성공적으로 완료되었습니다!"
}

# --- 메인 실행 흐름 ---
prepare_deployment
determine_ports
start_new_app
check_health
cleanup_old_app
