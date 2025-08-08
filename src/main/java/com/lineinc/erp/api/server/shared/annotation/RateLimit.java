package com.lineinc.erp.api.server.shared.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    /**
     * 초당 요청 횟수 제한 (기본값 10)
     */
    int limit() default 10;

    /**
     * 기간 단위 (초) 기본 60초 (1분)
     */
    int durationSeconds() default 60;
}