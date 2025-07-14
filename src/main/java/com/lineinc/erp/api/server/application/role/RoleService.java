package com.lineinc.erp.api.server.application.role;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
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
                .collect(Collectors.groupingBy(p -> p.getMenu().getId()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Long menuId = entry.getKey();
                    List<Permission> permissionList = entry.getValue();
                    String menuName = permissionList.get(0).getMenu().getName();
                    return MenusPermissionsResponse.from(menuId, menuName, permissionList);
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

        // 해당 role이 실제로 user의 roles에 있을 때만 제거 후 저장
        for (User user : users) {
            if (user.getRoles().contains(role)) {
                user.getRoles().remove(role);
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

            if (!user.getRoles().contains(role) && user.getRoles().isEmpty()) {
                user.getRoles().add(role);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void deleteRoleById(Long roleId) {
        Role role = getRoleOrThrow(roleId);

        List<User> users = userRepository.findAllByRoles_Id(roleId);
        for (User user : users) {
            user.getRoles().remove(role);
        }
        userRepository.saveAll(users);
        roleRepository.delete(role);
    }

    @Transactional
    public void deleteRolesByIds(List<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.ROLE_NOT_FOUND);
        }

        List<User> affectedUsers = userRepository.findAllByRoles_IdIn(roleIds);
        for (User user : affectedUsers) {
            user.getRoles().removeIf(role -> roleIds.contains(role.getId()));
        }
        userRepository.saveAll(affectedUsers);
        roleRepository.deleteAll(roles);
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
}
