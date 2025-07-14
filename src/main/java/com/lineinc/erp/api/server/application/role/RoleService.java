package com.lineinc.erp.api.server.application.role;

import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.MenusPermissionsResponse;
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

    @Transactional(readOnly = true)
    public Page<RolesResponse> getAllRoles(Pageable pageable) {
        return roleRepository.findAll((Object) null, pageable);
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
    public Role getRoleWithPermissionsAndMenus(Long roleId) {
        return roleRepository.findWithPermissionsAndMenusById(roleId)
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
}
