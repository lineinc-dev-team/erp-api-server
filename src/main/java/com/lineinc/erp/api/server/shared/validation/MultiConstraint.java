package com.lineinc.erp.api.server.shared.validation;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 여러 검증 유형을 하나의 어노테이션으로 처리하기 위한 커스텀 제약조건 어노테이션입니다.
 * <p>
 * 이 어노테이션은 다양한 검증 로직을 하나로 통합하여 재사용성을 높이고,
 * 필드나 메서드 파라미터에 쉽게 적용할 수 있도록 설계되었습니다.
 */
@Documented
@Constraint(validatedBy = MultiValidator.class) // 검증 로직을 수행하는 Validator 클래스를 지정
@Target({ElementType.FIELD, ElementType.PARAMETER}) // 필드 및 메서드 파라미터에 적용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 어노테이션 정보 유지
public @interface MultiConstraint {

    /**
     * 검증 실패 시 출력할 기본 메시지입니다.
     *
     * @return 실패 메시지 문자열
     */
    String message() default ValidationMessages.DEFAULT_INVALID_INPUT;

    /**
     * 검증 그룹을 지정할 때 사용되는 속성입니다.
     *
     * @return 검증 그룹 클래스 배열
     */
    Class<?>[] groups() default {};

    /**
     * 페이로드 정보를 지정하는 속성입니다.
     *
     * @return 페이로드 클래스 배열
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 실제 어떤 검증 유형을 적용할지 지정하는 필수 속성입니다.
     *
     * @return 검증 타입(enum)
     */
    ValidatorType type();
}