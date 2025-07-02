package com.lineinc.erp.api.server.common.validation;

import com.lineinc.erp.api.server.exception.GlobalExceptionHandler;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 여러 검증 유형을 하나의 어노테이션으로 처리하기 위한 커스텀 제약조건 어노테이션입니다.
 */

@Documented
@Constraint(validatedBy = MultiValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiConstraint {

    /**
     * 검증 실패 시 기본 메시지
     */
    String message() default "입력값이 유효하지 않습니다.";

    /**
     * 검증 그룹 지정용
     */
    Class<?>[] groups() default {};

    /**
     * 페이로드 정보
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 검증 타입 지정
     */
    ValidatorType type();
}