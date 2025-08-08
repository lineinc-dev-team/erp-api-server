package com.lineinc.erp.api.server.domain.role.service;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.entity.RolePermission;
import com.lineinc.erp.api.server.domain.role.entity.RoleSiteProcess;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
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
import java.util.Comparator;


@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final SiteRepository siteRepository;
    private final SiteProcessRepository siteProcessRepository;

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

        // Permission ID 기준으로 중복 제거
        Map<Long, Permission> uniquePermissions = role.getPermissions().stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toMap(
                        Permission::getId,
                        p -> p,
                        (existing, replacement) -> existing // 중복 시 기존 값 유지
                ));

        // Menu ID로 그룹핑
        return uniquePermissions.values().stream()
                .collect(Collectors.groupingBy(p -> p.getMenu().getId()))
                .entrySet().stream()
                .map(entry -> {
                    var permissions = entry.getValue().stream()
                            .sorted(Comparator.comparing(Permission::getId))
                            .map(MenusPermissionsResponse.PermissionDto::from)
                            .toList();

                    String menuName = entry.getValue().get(0).getMenu().getName();
                    return MenusPermissionsResponse.from(entry.getKey(), menuName, permissions);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleUserListResponse> getUsersByRoleId(Long roleId, RoleUserListRequest request, Pageable pageable) {
        return roleRepository.findUsersByRoleId(roleId, request, pageable);
    }

    @Transactional
    public void removeUsersFromRole(Long roleId, RemoveUsersFromRoleRequest request) {
        if (roleId != 1L) {
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
    }

    @Transactional
    public void addUsersToRole(Long roleId, AddUsersToRoleRequest request) {
        if (roleId != 1L) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

            Map<Long, String> memoMap = request.users().stream()
                    .collect(Collectors.toMap(AddUsersToRoleRequest.UserWithMemo::userId, AddUsersToRoleRequest.UserWithMemo::memo));

            List<Long> userIds = request.users().stream()
                    .map(AddUsersToRoleRequest.UserWithMemo::userId)
                    .toList();

            List<User> users = userRepository.findAllById(userIds);

            for (User user : users) {
                if (!user.getUserRoles().isEmpty()) {
                    continue;
                }

                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .memo(memoMap.get(user.getId()))
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
        List<Long> filteredRoleIds = roleIds.stream()
                .filter(id -> !id.equals(1L))
                .toList();

        List<Role> roles = roleRepository.findAllById(filteredRoleIds);

        if (roleIds.size() != roles.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND);
        }

        List<UserRole> userRoles = userRoleRepository.findByRole_IdIn(filteredRoleIds);
        for (UserRole userRole : userRoles) {
            userRole.markAsDeleted();
        }
        userRoleRepository.saveAll(userRoles);

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
                .memo(request.memo())
                .hasGlobalSiteProcessAccess(Boolean.TRUE.equals(request.hasGlobalSiteProcessAccess()))
                .build();

        roleRepository.save(newRole);

        // 유저 연결
        if (request.users() != null && !request.users().isEmpty()) {
            List<Long> userIds = request.users().stream().map(CreateRolesRequest.UserWithMemo::userId).toList();
            Map<Long, String> memoMap = request.users().stream()
                    .collect(Collectors.toMap(CreateRolesRequest.UserWithMemo::userId, CreateRolesRequest.UserWithMemo::memo));

            List<User> users = userRepository.findAllById(userIds);
            for (User user : users) {
                if (!user.getUserRoles().isEmpty()) {
                    continue;
                }

                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(newRole)
                        .memo(memoMap.get(user.getId()))
                        .build();
                user.getUserRoles().add(userRole);
                userRepository.save(user);
            }
        }

        // 권한 연결
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
            List<RolePermission> rolePermissions = permissions.stream()
                    .filter(permission -> permission.getMenu() != null)
                    .map(permission -> RolePermission.builder()
                            .role(newRole)
                            .permission(permission)
                            .build())
                    .collect(Collectors.toList());
            newRole.getPermissions().addAll(rolePermissions);
            roleRepository.save(newRole);
        }

        // 현장/공정 연결
        if (!Boolean.TRUE.equals(request.hasGlobalSiteProcessAccess())
                && request.siteProcesses() != null && !request.siteProcesses().isEmpty()) {

            List<RoleSiteProcess> siteProcesses = request.siteProcesses().stream()
                    .map(dto -> {
                        Site site = null;
                        if (dto.siteId() != null) {
                            site = siteRepository.findById(dto.siteId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
                        }
                        SiteProcess process = null;
                        if (dto.processId() != null) {
                            process = siteProcessRepository.findById(dto.processId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_PROCESS_NOT_FOUND));
                        }
                        if (site != null && process != null && !process.getSite().getId().equals(site.getId())) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
                        }
                        return RoleSiteProcess.builder()
                                .role(newRole)
                                .site(site)
                                .process(process)
                                .build();
                    })
                    .collect(Collectors.toList());
            newRole.getSiteProcesses().addAll(siteProcesses);
            roleRepository.save(newRole);
        }
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
    public boolean hasPermission(Long userId, String menuName, PermissionAction action) {
        User user = userRepository.findByIdWithPermissions(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .flatMap(role -> role.getPermissions().stream())
                .map(RolePermission::getPermission)
                .anyMatch(permission ->
                        permission.getMenu() != null &&
                                permission.getMenu().getName().equalsIgnoreCase(menuName.trim()) &&
                                PermissionAction.fromLabel(permission.getAction().getLabel()) == action
                );
    }

    @Transactional
    public void updateRole(Long roleId, UpdateRolesRequest request) {
        if (roleId == 1L) return;

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

        role.updateFrom(request);

        // 사용자 연결 처리
        if (request.users() != null) {
            // 기존 연결 제거 (bulk delete)
            userRoleRepository.deleteAllByRoleId(roleId);
            role.getUserRoles().clear();

            // 새로운 사용자 연결
            if (!request.users().isEmpty()) {
                List<Long> userIds = request.users().stream()
                        .map(UpdateRolesRequest.UserWithMemo::userId)
                        .toList();

                Map<Long, String> memoMap = request.users().stream()
                        .collect(Collectors.toMap(
                                UpdateRolesRequest.UserWithMemo::userId,
                                UpdateRolesRequest.UserWithMemo::memo
                        ));

                List<User> users = userRepository.findAllById(userIds);
                List<UserRole> newUserRoles = users.stream()
                        .filter(user -> user.getUserRoles().isEmpty())
                        .map(user -> UserRole.builder()
                                .user(user)
                                .role(role)
                                .memo(memoMap.get(user.getId()))
                                .build())
                        .toList();

                if (!newUserRoles.isEmpty()) {
                    userRoleRepository.saveAll(newUserRoles);
                    role.getUserRoles().addAll(newUserRoles);
                }
            }
        }

        // 권한 연결 처리
        if (request.permissionIds() != null) {
            role.getPermissions().clear();

            if (!request.permissionIds().isEmpty()) {
                List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
                if (permissions.size() != request.permissionIds().size()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SOME_PERMISSIONS_NOT_FOUND);
                }

                List<RolePermission> rolePermissions = permissions.stream()
                        .filter(p -> p.getMenu() != null)
                        .map(p -> RolePermission.builder()
                                .role(role)
                                .permission(p)
                                .build())
                        .collect(Collectors.toList());

                role.getPermissions().addAll(rolePermissions);
            }
        }

        // 현장/공정 연결 처리
        if (request.hasGlobalSiteProcessAccess() != null && !request.hasGlobalSiteProcessAccess()) {
            role.getSiteProcesses().clear();

            if (request.siteProcesses() != null && !request.siteProcesses().isEmpty()) {
                List<RoleSiteProcess> siteProcesses = request.siteProcesses().stream()
                        .map(dto -> {
                            Site site = dto.siteId() != null ?
                                    siteRepository.findById(dto.siteId())
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND))
                                    : null;

                            SiteProcess process = dto.processId() != null ?
                                    siteProcessRepository.findById(dto.processId())
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_PROCESS_NOT_FOUND))
                                    : null;

                            if (site != null && process != null && !process.getSite().getId().equals(site.getId())) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
                            }

                            return RoleSiteProcess.builder()
                                    .role(role)
                                    .site(site)
                                    .process(process)
                                    .build();
                        })
                        .collect(Collectors.toList());

                role.getSiteProcesses().addAll(siteProcesses);
            }
        }

        roleRepository.save(role);
    }
}
