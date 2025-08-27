package com.lineinc.erp.api.server.infrastructure.config.aop;

import com.lineinc.erp.api.server.domain.common.service.RateLimitService;
import com.lineinc.erp.api.server.shared.annotation.RateLimit;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.SecurityUtils;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    /**
     * RateLimit 애노테이션이 붙은 메서드 실행 전후에 호출되어,
     * 사용자별 요청 제한을 검사하는 AOP 메서드입니다.
     *
     * @param joinPoint 실제 실행될 메서드의 정보를 담고 있는 객체
     * @return 메서드 실행 결과 객체
     * @throws Throwable 메서드 실행 중 발생하는 예외 전달
     */
    @Around("@annotation(com.lineinc.erp.api.server.common.annotation.RateLimit)")
    public Object rateLimitCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = signature.getMethod().getAnnotation(RateLimit.class);

        // 사용자 식별자 추출 (로그인 사용자 식별용)
        String resolvedUserId = SecurityUtils.resolveCurrentUsername();

        // 사용자 식별자 및 제한 설정 값으로 Bucket 생성 또는 가져오기
        Bucket bucket = rateLimitService.resolveBucket(
                resolvedUserId,
                rateLimit.limit(), // 허용 요청 수
                rateLimit.durationSeconds() // 제한 시간 (초)
        );

        // 요청 가능 여부 확인 후 처리
        if (bucket.tryConsume(1)) {
            // 버킷에서 1개의 토큰 소모 성공 → 요청 허용
            return joinPoint.proceed();
        } else {
            // 버킷이 비어 있음 → 요청 거부 (429 Too Many Requests)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, ValidationMessages.RATE_LIMIT_EXCEEDED);
        }
    }
}