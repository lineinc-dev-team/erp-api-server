package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class MenuPermissionAspect {

    private final RoleService roleService;

    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(RequireMenuPermission requireMenuPermission) {
        Long userId = getUserId();
        log.info("[권한 체크] 권한 검사 시작: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(), requireMenuPermission.action());

        List<MenusPermissionsResponse> menusPermissionsResponses = roleService.getPermissionsById(userId);

        boolean hasPermission = menusPermissionsResponses.stream()
                .filter(p -> p.name().trim().equalsIgnoreCase(requireMenuPermission.menu().trim()))
                .flatMap(p -> p.permissions().stream())
                .anyMatch(permission ->
                        PermissionAction.fromLabel(permission.action()) == requireMenuPermission.action());

        if (!hasPermission) {
            log.info("[권한 체크] 권한 없음: userId={}, menu={}, action={}", userId, requireMenuPermission.menu(), requireMenuPermission.action());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
    }

    private static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ValidationMessages.INVALID_USER_INFO);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId();
        } else if (principal instanceof User) {
            return ((User) principal).getId();
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "지원되지 않는 Principal 타입입니다: " + principal.getClass().getName());
        }
    }
}