package com.lineinc.erp.api.server.domain.common.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RateLimitService {

    // 상수 정의
    private static final int CLEANUP_INTERVAL_MINUTES = 30;
    private static final int MAX_BUCKET_AGE_MINUTES = 60;
    private static final long CLEANUP_INTERVAL_MILLIS = CLEANUP_INTERVAL_MINUTES * 60L * 1000L;

    // 사용자별 요청 제한 정보를 저장하는 Map (Thread-safe)
    // key: 사용자 ID, value: 해당 사용자의 요청 제한 버킷(Bucket)
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    // 버킷 생성 시간을 추적하는 Map (메모리 정리용)
    private final Map<String, OffsetDateTime> bucketCreationTimes = new ConcurrentHashMap<>();

    /**
     * 사용자 ID에 맞는 요청 제한 버킷을 반환합니다.
     * 만약 해당 사용자의 버킷이 없으면 새로 생성 후 저장합니다.
     *
     * @param userId          요청하는 사용자의 식별자(ID)
     * @param limit           제한할 최대 요청 횟수 (durationSeconds 기간 동안 허용 가능한 최대 요청 수)
     * @param durationSeconds 요청 제한이 적용되는 시간 간격(초)
     * @return 해당 사용자의 요청 제한 Bucket 객체
     */
    public Bucket resolveBucket(String userId, int limit, int durationSeconds) {
        // userBuckets 맵에 userId 키가 없으면 새 버킷을 생성하여 저장하고, 있으면 기존 버킷을 반환
        return userBuckets.computeIfAbsent(userId, key -> {
            log.debug("새로운 Rate Limit 버킷 생성: userId={}, limit={}, duration={}초", key, limit, durationSeconds);
            bucketCreationTimes.put(key, OffsetDateTime.now());
            return createNewBucket(limit, durationSeconds);
        });
    }

    /**
     * 새로운 요청 제한 버킷(Bucket)을 생성합니다.
     * 이 버킷은 limit 횟수만큼 요청을 허용하며,
     * limit 횟수가 모두 소모되면 durationSeconds 초가 지나야 다시 토큰이 채워집니다.
     *
     * @param limit           허용할 최대 요청 횟수
     * @param durationSeconds 토큰이 모두 소모된 후 재충전까지 걸리는 시간 (초 단위)
     * @return 생성된 Bucket 객체
     */
    private Bucket createNewBucket(int limit, int durationSeconds) {
        // Bandwidth 클래식 모드를 사용하여, limit 횟수만큼 요청을 허용하고
        // durationSeconds 초마다 limit 토큰을 한번에 재충전하는 규칙 생성
        Bandwidth limitBandwidth = Bandwidth.classic(
                limit,
                Refill.intervally(limit, Duration.ofSeconds(durationSeconds)));

        // 위의 Bandwidth 규칙을 가진 Bucket 생성 후 반환
        return Bucket.builder()
                .addLimit(limitBandwidth)
                .build();
    }

    /**
     * 오래된 버킷들을 정리하는 스케줄된 작업
     * 메모리 누수를 방지하기 위해 주기적으로 실행됩니다.
     */
    @Scheduled(fixedRate = CLEANUP_INTERVAL_MILLIS)
    public void cleanupOldBuckets() {
        OffsetDateTime cutoffTime = OffsetDateTime.now().minusMinutes(MAX_BUCKET_AGE_MINUTES);
        int initialSize = userBuckets.size();

        // 오래된 버킷들 제거
        bucketCreationTimes.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoffTime)) {
                userBuckets.remove(entry.getKey());
                return true;
            }
            return false;
        });

        int removedCount = initialSize - userBuckets.size();
        if (removedCount > 0) {
            log.info("오래된 Rate Limit 버킷 {}개 정리 완료. 현재 버킷 수: {}", removedCount, userBuckets.size());
        }
    }
}