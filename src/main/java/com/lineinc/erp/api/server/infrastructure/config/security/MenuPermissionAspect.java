package com.lineinc.erp.api.server.infrastructure.config.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.role.service.v1.RoleService;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class MenuPermissionAspect {

    private final RoleService roleService;

    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(final RequireMenuPermission requireMenuPermission) {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long userId = ((CustomUserDetails) principal).getUserId();
        log.info("[권한 체크] 권한 검사 시작: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(),
                requireMenuPermission.action());
        final boolean hasPermission = roleService.hasPermission(
                userId,
                requireMenuPermission.menu(),
                requireMenuPermission.action());
        if (!hasPermission) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
        log.info("[권한 체크] 권한 검사 통과: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(),
                requireMenuPermission.action());
    }
}