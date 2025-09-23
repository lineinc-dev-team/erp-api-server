package com.lineinc.erp.api.server.domain.role.service.v1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
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
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse.RoleSummaryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.AddPermissionsToRoleRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.AddUsersToRoleRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.CreateRolesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.RemoveUsersFromRoleRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UpdateRolesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.MenusPermissionsResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RolesResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;

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
    public Page<RolesResponse> getAllRoles(final UserWithRolesListRequest request, final Pageable pageable) {
        return roleRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RoleSummaryResponse> searchRolesByName(final String keyword, final Pageable pageable) {
        final Page<Role> roles = roleRepository.searchRoles(keyword, pageable);
        return roles.map(role -> new RoleSummaryResponse(role.getId(), role.getName(), role.isDeleted()));
    }

    @Transactional(readOnly = true)
    public RolesResponse getRoleById(final Long roleId) {
        final Role role = roleRepository.findRoleWithDetails(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return RolesResponse.from(role);
    }

    @Transactional(readOnly = true)
    public Role getRoleOrThrow(final Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenusPermissionsResponse> getMenusPermissionsById(final Long roleId) {
        final Role role = roleRepository.findRoleWithPermissions(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 권한을 메뉴별로 그룹화하고 정렬
        final Map<Long, List<Permission>> groupedByMenu = role.getPermissions().stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.groupingBy(permission -> permission.getMenu().getId()));

        return groupedByMenu.entrySet().stream()
                .map(entry -> {
                    final Long menuId = entry.getKey();
                    final List<Permission> permissions = entry.getValue();

                    // 메뉴 정보
                    final Menu menu = permissions.get(0).getMenu();

                    // 권한 정렬 및 DTO 변환
                    final List<MenusPermissionsResponse.PermissionDto> sortedPermissions = permissions.stream()
                            .sorted(Comparator.comparing(Permission::getOrder))
                            .map(MenusPermissionsResponse.PermissionDto::from)
                            .toList();

                    return MenusPermissionsResponse.from(menuId, menu.getName(), sortedPermissions);
                })
                .sorted(Comparator.comparing(response -> {
                    // 메뉴 order로 정렬
                    return groupedByMenu.get(response.id()).get(0).getMenu().getOrder();
                }))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleUserListResponse> getUsersByRoleId(final Long roleId, final RoleUserListRequest request,
            final Pageable pageable) {
        return roleRepository.findUsersByRoleId(roleId, request, pageable);
    }

    @Transactional
    public void removeUsersFromRole(final Long roleId, final RemoveUsersFromRoleRequest request) {
        if (roleId != 1L) {
            final Role role = roleRepository.findById(roleId)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

            final List<User> users = userRepository.findAllById(request.userIds());

            // 해당 role이 실제로 user의 userRoles에 있을 때만 제거 후 저장
            for (final User user : users) {
                if (user.getUserRoles().stream().anyMatch(ur -> ur.getRole().equals(role))) {
                    user.getUserRoles().removeIf(ur -> ur.getRole().equals(role));
                    userRepository.save(user);
                }
            }
        }
    }

    @Transactional
    public void addUsersToRole(final Long roleId, final AddUsersToRoleRequest request) {
        if (roleId != 1L) {
            final Role role = roleRepository.findById(roleId)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));

            final Map<Long, String> memoMap = request.users().stream()
                    .collect(Collectors.toMap(AddUsersToRoleRequest.UserWithMemo::userId,
                            AddUsersToRoleRequest.UserWithMemo::memo));

            final List<Long> userIds = request.users().stream()
                    .map(AddUsersToRoleRequest.UserWithMemo::userId)
                    .toList();

            final List<User> users = userRepository.findAllById(userIds);

            for (final User user : users) {
                if (!user.getUserRoles().isEmpty()) {
                    continue;
                }

                final UserRole userRole = UserRole.builder()
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
    public void deleteRoleById(final Long roleId) {
        final Role role = getRoleOrThrow(roleId);

        final List<UserRole> userRoles = userRoleRepository.findByRole_Id(roleId);
        for (final UserRole userRole : userRoles) {
            final User user = userRole.getUser();
            user.getUserRoles().remove(userRole);
        }
        userRepository.saveAll(userRoles.stream()
                .map(UserRole::getUser)
                .distinct()
                .toList());

        roleRepository.delete(role);
    }

    @Transactional
    public void deleteRolesByIds(final List<Long> roleIds) {
        final List<Long> filteredRoleIds = roleIds.stream()
                .filter(id -> !id.equals(1L))
                .toList();

        final List<Role> roles = roleRepository.findAllById(filteredRoleIds);

        if (roleIds.size() != roles.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND);
        }

        final List<UserRole> userRoles = userRoleRepository.findByRole_IdIn(filteredRoleIds);
        for (final UserRole userRole : userRoles) {
            userRole.markAsDeleted();
        }
        userRoleRepository.saveAll(userRoles);

        for (final Role role : roles) {
            role.markAsDeleted();
        }
        roleRepository.saveAll(roles);
    }

    @Transactional
    public void createRole(final CreateRolesRequest request) {
        // 권한 그룹명 중복 체크
        validateRoleName(request.name());

        // 권한 그룹 생성
        final Role newRole = createRoleEntity(request);
        roleRepository.save(newRole);

        // 사용자 연결
        connectUsersToRole(newRole, request.users());

        // 권한 연결
        connectPermissionsToRole(newRole, request.permissionIds());

        // 현장/공정 연결
        connectSiteProcessesToRole(newRole, request.hasGlobalSiteProcessAccess(), request.siteProcesses());

        // 최종 저장
        roleRepository.save(newRole);
    }

    private void validateRoleName(final String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.ROLE_NAME_ALREADY_EXISTS);
        }
    }

    private Role createRoleEntity(final CreateRolesRequest request) {
        return Role.builder()
                .name(request.name())
                .memo(request.memo())
                .hasGlobalSiteProcessAccess(Boolean.TRUE.equals(request.hasGlobalSiteProcessAccess()))
                .build();
    }

    private void connectUsersToRole(final Role role, final List<CreateRolesRequest.UserWithMemo> users) {
        if (users == null || users.isEmpty()) {
            return;
        }

        final List<Long> userIds = users.stream()
                .map(CreateRolesRequest.UserWithMemo::userId)
                .toList();

        final Map<Long, String> memoMap = users.stream()
                .collect(Collectors.toMap(
                        CreateRolesRequest.UserWithMemo::userId,
                        CreateRolesRequest.UserWithMemo::memo));

        final List<User> foundUsers = userRepository.findAllById(userIds);

        foundUsers.stream()
                .filter(user -> user.getUserRoles().stream()
                        .noneMatch(ur -> !ur.getRole().isDeleted())) // 삭제되지 않은 역할이 없는 사용자만
                .forEach(user -> {
                    final UserRole userRole = UserRole.builder()
                            .user(user)
                            .role(role)
                            .memo(memoMap.get(user.getId()))
                            .build();
                    user.getUserRoles().add(userRole);
                    userRepository.save(user);
                });
    }

    private void connectPermissionsToRole(final Role role, final List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        final List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        final List<RolePermission> rolePermissions = permissions.stream()
                .filter(permission -> permission.getMenu() != null)
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toList());

        role.getPermissions().addAll(rolePermissions);
    }

    private void connectSiteProcessesToRole(final Role role, final Boolean hasGlobalAccess,
            final List<CreateRolesRequest.SiteProcessAccess> siteProcesses) {
        if (siteProcesses == null || siteProcesses.isEmpty()) {
            return;
        }

        // hasGlobalAccess가 true여도 siteProcesses가 제공된 경우 유효성 검증 필요
        final List<RoleSiteProcess> roleSiteProcesses = siteProcesses.stream()
                .map(dto -> createRoleSiteProcess(role, dto))
                .collect(Collectors.toList());

        role.getSiteProcesses().addAll(roleSiteProcesses);
    }

    private RoleSiteProcess createRoleSiteProcess(final Role role, final CreateRolesRequest.SiteProcessAccess dto) {
        final Site site = findSiteById(dto.siteId());
        final SiteProcess process = findSiteProcessById(dto.processId());

        validateSiteProcessMatch(site, process);

        return RoleSiteProcess.builder()
                .role(role)
                .site(site)
                .process(process)
                .build();
    }

    private Site findSiteById(final Long siteId) {
        if (siteId == null) {
            return null;
        }
        return siteRepository.findById(siteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
    }

    private SiteProcess findSiteProcessById(final Long processId) {
        if (processId == null) {
            return null;
        }
        return siteProcessRepository.findById(processId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.SITE_PROCESS_NOT_FOUND));
    }

    private void validateSiteProcessMatch(final Site site, final SiteProcess process) {
        if (site != null && process != null && !process.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }
    }

    @Transactional
    public void setPermissionsToRole(final Long roleId, final AddPermissionsToRoleRequest request) {
        final Role role = getRoleOrThrow(roleId);

        final List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
        if (permissions.size() != request.permissionIds().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SOME_PERMISSIONS_NOT_FOUND);
        }

        // 기존 권한 제거 후 새로 설정
        permissionRepository.deleteAllByRoleIdNative(roleId);
        role.getPermissions().clear();

        final List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toList());

        role.getPermissions().addAll(rolePermissions);
        roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(final Long userId, final String menuName, final PermissionAction action) {
        // 최적화된 권한 체크 쿼리 사용
        return userRepository.hasPermission(userId, menuName.trim(), action);
    }

    @Transactional
    public void updateRole(final Long roleId, final UpdateRolesRequest request) {
        final Role role = findRoleById(roleId);

        // 이름 변경 시 중복 체크
        validateRoleNameIfChanged(role.getName(), request.name());

        // 기본 정보 업데이트
        role.updateFrom(request);

        // 사용자 연결 업데이트
        updateUserConnections(role, request.users());

        // 권한 연결 업데이트
        updatePermissionConnections(role, request.permissionIds());

        // 현장/공정 연결 업데이트
        updateSiteProcessConnections(role, request.hasGlobalSiteProcessAccess(), request.siteProcesses());

        roleRepository.save(role);
    }

    private Role findRoleById(final Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND));
    }

    private void validateRoleNameIfChanged(final String currentName, final String newName) {
        if (newName != null && !newName.equals(currentName)) {
            if (roleRepository.existsByName(newName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.ROLE_NAME_ALREADY_EXISTS);
            }
        }
    }

    private void updateUserConnections(final Role role, final List<UpdateRolesRequest.UserWithMemo> users) {
        if (users == null) {
            return;
        }

        // 기존 연결 제거
        userRoleRepository.deleteAllByRoleId(role.getId());
        role.getUserRoles().clear();

        // 새로운 사용자 연결
        if (!users.isEmpty()) {
            final List<UserRole> newUserRoles = createUserRoles(role, users);
            if (!newUserRoles.isEmpty()) {
                userRoleRepository.saveAll(newUserRoles);
                role.getUserRoles().addAll(newUserRoles);
            }
        }
    }

    private List<UserRole> createUserRoles(final Role role, final List<UpdateRolesRequest.UserWithMemo> users) {
        final List<Long> userIds = users.stream()
                .map(UpdateRolesRequest.UserWithMemo::userId)
                .toList();

        final Map<Long, String> memoMap = users.stream()
                .collect(Collectors.toMap(
                        UpdateRolesRequest.UserWithMemo::userId,
                        UpdateRolesRequest.UserWithMemo::memo));

        final List<User> foundUsers = userRepository.findAllById(userIds);

        return foundUsers.stream()
                .filter(user -> user.getUserRoles().isEmpty())
                .map(user -> UserRole.builder()
                        .user(user)
                        .role(role)
                        .memo(memoMap.get(user.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private void updatePermissionConnections(final Role role, final List<Long> permissionIds) {
        if (permissionIds == null) {
            return;
        }

        role.getPermissions().clear();

        if (!permissionIds.isEmpty()) {
            final List<Permission> permissions = findPermissionsByIds(permissionIds);
            final List<RolePermission> rolePermissions = createRolePermissions(role, permissions);
            role.getPermissions().addAll(rolePermissions);
        }
    }

    private List<Permission> findPermissionsByIds(final List<Long> permissionIds) {
        final List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SOME_PERMISSIONS_NOT_FOUND);
        }
        return permissions;
    }

    private List<RolePermission> createRolePermissions(final Role role, final List<Permission> permissions) {
        return permissions.stream()
                .filter(permission -> permission.getMenu() != null)
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateSiteProcessConnections(final Role role, final Boolean hasGlobalAccess,
            final List<UpdateRolesRequest.SiteProcessAccess> siteProcesses) {
        // hasGlobalAccess가 true여도 siteProcesses가 제공된 경우 유효성 검증 필요
        if (siteProcesses != null && !siteProcesses.isEmpty()) {
            // siteProcesses가 제공된 경우 항상 유효성 검증 수행
            final List<RoleSiteProcess> newSiteProcesses = createRoleSiteProcesses(role, siteProcesses);

            // hasGlobalAccess가 false인 경우에만 기존 연결을 새로 설정
            if (hasGlobalAccess != null && !hasGlobalAccess) {
                role.getSiteProcesses().clear();
                role.getSiteProcesses().addAll(newSiteProcesses);
            }
        } else if (hasGlobalAccess != null && !hasGlobalAccess) {
            // siteProcesses가 없고 hasGlobalAccess가 false인 경우 기존 연결만 제거
            role.getSiteProcesses().clear();
        }
    }

    private List<RoleSiteProcess> createRoleSiteProcesses(final Role role,
            final List<UpdateRolesRequest.SiteProcessAccess> siteProcesses) {
        return siteProcesses.stream()
                .map(dto -> {
                    final Site site = dto.siteId() != null ? siteRepository.findById(dto.siteId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    ValidationMessages.SITE_NOT_FOUND))
                            : null;

                    final SiteProcess process = dto.processId() != null
                            ? siteProcessRepository.findById(dto.processId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            ValidationMessages.SITE_PROCESS_NOT_FOUND))
                            : null;

                    validateSiteProcessMatch(site, process);

                    return RoleSiteProcess.builder()
                            .role(role)
                            .site(site)
                            .process(process)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
