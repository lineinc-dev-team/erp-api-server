package com.lineinc.erp.api.server.presentation.v1.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "사용자 검색 및 페이징 요청")
public record UserListRequest(
) {
}