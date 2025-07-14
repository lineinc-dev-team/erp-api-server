package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;

import java.lang.annotation.*;

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
