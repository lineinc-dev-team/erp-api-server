package com.lineinc.erp.api.server.domain.menu.service;

import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.menu.dto.response.MenuWithPermissionsResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    /**
     * 모든 메뉴와 메뉴별 권한을 포함하여 반환
     */
    public List<MenuWithPermissionsResponse> getMenusWithPermissions() {
        return menuRepository.findAllWithPermissions().stream()
                .map(MenuWithPermissionsResponse::from)
                .collect(Collectors.toList());
    }

}