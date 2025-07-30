package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class MenuPermissionAspect {

    private final RoleService roleService;

    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(RequireMenuPermission requireMenuPermission) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((CustomUserDetails) principal).getUserId();
        log.info("[권한 체크] 권한 검사 시작: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(), requireMenuPermission.action());
        boolean hasPermission = roleService.hasPermission(
                userId,
                requireMenuPermission.menu(),
                requireMenuPermission.action()
        );
        if (!hasPermission) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
        log.info("[권한 체크] 권한 검사 통과: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(), requireMenuPermission.action());
    }
}