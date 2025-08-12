package com.lineinc.erp.api.server.presentation.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;


@ParameterObject
@Schema(description = "사용자 검색 및 해당 사용자들의 권한 그룹 조회 요청")
public record UserWithRolesListRequest(
        @Schema(description = "사용자 이름 또는 로그인 ID 검색어", example = "홍길동")
        String userSearch
) {
}