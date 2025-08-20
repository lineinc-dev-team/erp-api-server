package com.lineinc.erp.api.server.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "정렬 요청")
public record SortRequest(
        @Schema(description = """
                허용 필드 예시: id, name, createdAt, updatedAt.
                정렬 방향은 'asc' (오름차순) 또는 'desc' (내림차순) 사용.
                API별로 허용하는 필드는 다를 수 있습니다.
                """, example = "id,asc") String sort) {
    public SortRequest {
        if (sort == null)
            sort = "id,asc";
    }
}