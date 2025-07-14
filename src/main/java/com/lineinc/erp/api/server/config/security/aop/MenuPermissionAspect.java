package com.lineinc.erp.api.server.config.security.aop;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.application.role.RoleService;
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

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class MenuPermissionAspect {

    private final RoleService roleService;

    @Before("@annotation(requireMenuPermission)")
    public void checkMenuPermission(RequireMenuPermission requireMenuPermission) {
        // 현재 로그인한 사용자의 인증 정보를 가져옴
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증 객체가 User 인스턴스가 아닐 경우 예외 발생 (정상 로그인 사용자가 아님)
        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ValidationMessages.INVALID_USER_INFO);
        }

        List<Role> roles = user.getRoles().stream().toList();

        // 전체 권한 역할이 있는 경우 권한 체크를 생략
        boolean isMaster = roles.stream()
                .anyMatch(role -> AppConstants.ROLE_MASTER_NAME.equals(role.getName()));
        if (isMaster) {
            return;
        }

        // 각 역할에 대해 해당 메뉴와 권한 액션을 갖는지 검사
        boolean hasPermission = roles.stream()
                .flatMap(role -> roleService.getMenusPermissionsById(role.getId()).stream())
                // 메뉴 이름 비교 (대소문자 및 공백 무시)
                .filter(menuPermission -> menuPermission.name().trim().equalsIgnoreCase(requireMenuPermission.menu().trim()))
                .flatMap(menuPermission -> menuPermission.permissions().stream())
                // 권한 라벨을 PermissionAction enum으로 변환하여 일치 여부 확인
                .anyMatch(permission ->
                        PermissionAction.fromLabel(permission.action()) == requireMenuPermission.action());

        if (!hasPermission) {
            // 권한이 없을 경우 로그 출력 후 403 예외 발생
            log.info("권한 없음: user={}, menu={}, action={}", user.getUsername(), requireMenuPermission.menu(), requireMenuPermission.action());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.NO_MENU_PERMISSION);
        }
    }
}
