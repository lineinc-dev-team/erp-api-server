package com.lineinc.erp.api.server.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "공통 페이징 요청")
public record PageRequest(
        @Min(0)
        @Schema(description = "0부터 시작하는 페이지 번호", example = "0")
        Integer page,

        @Min(1)
        @Max(200)
        @Schema(description = "한 페이지에 포함될 아이템 수", example = "20")
        Integer size,

        @Schema(description = """
                허용 필드 예시: id, name, createdAt, updatedAt.
                정렬 방향은 'asc' (오름차순) 또는 'desc' (내림차순) 사용.
                API별로 허용하는 필드는 다를 수 있습니다.
                """,
                example = "id,asc"
        )
        String sort
) {
    public PageRequest {
        if (page == null) page = 0;
        if (size == null) size = 20;
        if (sort == null) sort = "id,asc";
    }
}