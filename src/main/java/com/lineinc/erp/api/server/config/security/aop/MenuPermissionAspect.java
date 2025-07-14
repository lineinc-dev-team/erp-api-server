package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.application.role.RoleService;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class MenuPermissionAspect {

    private final RoleService roleService;

    @Transactional(readOnly = true)
    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(RequireMenuPermission requireMenuPermission) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ValidationMessages.INVALID_USER_INFO);
        }

        List<Role> roles = user.getRoles().stream().toList();

        boolean hasPermission = roles.stream()
                .flatMap(role -> roleService.getMenusPermissionsById(role.getId()).stream())
                .filter(menuPermission -> menuPermission.name().trim().equalsIgnoreCase(requireMenuPermission.menu().trim()))
                .flatMap(menuPermission -> menuPermission.permissions().stream())
                .anyMatch(permission ->
                        PermissionAction.fromLabel(permission.action()) == requireMenuPermission.action());
        ;

        if (!hasPermission) {
            log.info("권한 없음: user={}, menu={}, action={}", user.getUsername(), requireMenuPermission.menu(), requireMenuPermission.action());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
    }
}
