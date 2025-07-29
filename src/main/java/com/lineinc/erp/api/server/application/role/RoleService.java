package com.lineinc.erp.api.server.application.role;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.entity.RolePermission;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserRoleRepository;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.*;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public Page<RolesResponse> getAllRoles(UserWithRolesListRequest request, Pageable pageable) {
        return roleRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public RolesResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return RolesResponse.from(role);
    }

    @Transactional(readOnly = true)
    public Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenusPermissionsResponse> getMenusPermissionsById(Long roleId) {
        Role role = roleRepository.findWithPermissionsAndMenusById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Map<Long, List<Permission>> grouped = role.getPermissions().stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.groupingBy(permission -> permission.getMenu().getId()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Long menuId = entry.getKey();
                    List<Permission> permissionList = entry.getValue();
                    String menuName = permissionList.get(0).getMenu().getName();

                    List<MenusPermissionsResponse.PermissionDto> permissionDtos = permissionList.stream()
                            .map(permission -> MenusPermissionsResponse.PermissionDto.from(permission))
                            .toList();

                    return MenusPermissionsResponse.from(menuId, menuName, permissionDtos);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleUserListResponse> getUsersByRoleId(Long roleId, RoleUserListRequest request, Pageable pageable) {
        return roleRepository.findUsersByRoleId(roleId, request, pageable);
    }

    @Transactional
    public void removeUsersFromRole(Long roleId, RemoveUsersFromRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

        List<User> users = userRepository.findAllById(request.userIds());

        // 해당 role이 실제로 user의 userRoles에 있을 때만 제거 후 저장
        for (User user : users) {
            if (user.getUserRoles().stream().anyMatch(ur -> ur.getRole().equals(role))) {
                user.getUserRoles().removeIf(ur -> ur.getRole().equals(role));
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void addUsersToRole(Long roleId, AddUsersToRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

        List<User> users = userRepository.findAllById(request.userIds());

        for (User user : users) {
            boolean hasRole = user.getUserRoles().stream().anyMatch(ur -> ur.getRole().equals(role));
            if (!hasRole) {
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                user.getUserRoles().add(userRole);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void deleteRoleById(Long roleId) {
        Role role = getRoleOrThrow(roleId);

        List<UserRole> userRoles = userRoleRepository.findByRole_Id(roleId);
        for (UserRole userRole : userRoles) {
            User user = userRole.getUser();
            user.getUserRoles().remove(userRole);
        }
        userRepository.saveAll(userRoles.stream()
                .map(UserRole::getUser)
                .distinct()
                .toList());

        roleRepository.delete(role);
    }

    @Transactional
    public void deleteRolesByIds(List<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND);
        }

        List<UserRole> userRoles = userRoleRepository.findByRole_IdIn(roleIds);
        for (UserRole userRole : userRoles) {
            User user = userRole.getUser();
            user.getUserRoles().remove(userRole);
        }
        userRepository.saveAll(userRoles.stream()
                .map(UserRole::getUser)
                .distinct()
                .toList());

        for (Role role : roles) {
            role.markAsDeleted();
        }
        roleRepository.saveAll(roles);
    }

    @Transactional
    public void createRole(CreateRolesRequest request) {
        boolean exists = roleRepository.existsByName(request.name());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.ROLE_NAME_ALREADY_EXISTS);
        }

        Role newRole = Role.builder()
                .name(request.name())
                .build();

        roleRepository.save(newRole);
    }

    @Transactional
    public void setPermissionsToRole(Long roleId, AddPermissionsToRoleRequest request) {
        Role role = getRoleOrThrow(roleId);

        List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
        if (permissions.size() != request.permissionIds().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SOME_PERMISSIONS_NOT_FOUND);
        }

        // 기존 권한 제거 후 새로 설정
        permissionRepository.deleteAllByRoleIdNative(roleId);
        role.getPermissions().clear();

        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toList());

        role.getPermissions().addAll(rolePermissions);
        roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<MenusPermissionsResponse> getPermissionsById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        List<Long> roleIds = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getId())
                .distinct()
                .toList();

        if (roleIds.isEmpty()) {
            return List.of();
        }

        List<MenusPermissionsResponse> allPermissions = roleIds.stream()
                .flatMap(roleId -> getMenusPermissionsById(roleId).stream())
                .toList();

        Map<Long, List<MenusPermissionsResponse>> groupedByMenuId = allPermissions.stream()
                .collect(Collectors.groupingBy(MenusPermissionsResponse::id));

        return groupedByMenuId.entrySet().stream()
                .map(entry -> {
                    Long menuId = entry.getKey();
                    List<MenusPermissionsResponse> permList = entry.getValue();

                    String menuName = permList.get(0).name();

                    List<MenusPermissionsResponse.PermissionDto> combinedPermissions = permList.stream()
                            .flatMap(mp -> mp.permissions().stream())
                            .distinct()
                            .toList();

                    return MenusPermissionsResponse.from(menuId, menuName, combinedPermissions);
                })
                .toList();
    }
}
