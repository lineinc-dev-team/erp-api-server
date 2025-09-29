package com.lineinc.erp.api.server.infrastructure.config.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireMenuPermission {
    /**
     * 권한을 검사할 메뉴 이름 (예: "권한관리")
     */
    String menu();

    /**
     * 검사할 권한 액션 (예: PermissionAction.VIEW)
     */
    PermissionAction action();
}
