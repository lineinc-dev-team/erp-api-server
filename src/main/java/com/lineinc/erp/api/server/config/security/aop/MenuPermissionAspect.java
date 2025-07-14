package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuPermissionAspect {

    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(RequireMenuPermission requireMenuPermission) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ValidationMessages.INVALID_USER_INFO);
        }

        System.out.println("principal = " + principal);

        boolean hasPermission = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission ->
                        permission.getMenu().getName().equals(requireMenuPermission.menu())
                                && permission.getAction() == requireMenuPermission.action()
                );

        if (!hasPermission) {
            log.warn("권한 없음: user={}, menu={}, action={}", user.getUsername(), requireMenuPermission.menu(), requireMenuPermission.action());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
    }
}
