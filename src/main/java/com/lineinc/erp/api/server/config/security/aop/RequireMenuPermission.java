package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireMenuPermission {
    String menu();

    PermissionAction action();
}
