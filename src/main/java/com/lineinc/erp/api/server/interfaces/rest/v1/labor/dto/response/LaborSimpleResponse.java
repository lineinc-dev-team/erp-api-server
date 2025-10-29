package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 요약 응답")
public record LaborSimpleResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "이름", example = "김철근") String name,
        @Schema(description = "노무 구분", example = "건축") String type,
        @Schema(description = "노무 구분 코드", example = "CONSTRUCTION") LaborType typeCode,
        @Schema(description = "노무 구분 설명", example = "건축 공종") String typeDescription,
        @Schema(description = "공종", example = "건축") String workType,
        @Schema(description = "공종 코드", example = "CONSTRUCTION") LaborWorkType workTypeCode,
        @Schema(description = "공종 설명", example = "건축 공종") String workTypeDescription,
        @Schema(description = "삭제 여부", example = "false") Boolean deleted) {

    public static LaborSimpleResponse from(final Labor labor) {
        return new LaborSimpleResponse(
                labor.getId(),
                labor.getName(),
                labor.getType() != null ? labor.getType().getLabel() : null,
                labor.getType(),
                labor.getTypeDescription(),
                labor.getWorkType() != null ? labor.getWorkType().getLabel() : null,
                labor.getWorkType(),
                labor.getWorkTypeDescription(),
                labor.isDeleted());
    }
}
