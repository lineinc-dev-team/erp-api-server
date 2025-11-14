package com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response;

import com.lineinc.erp.api.server.domain.organization.entity.Grade;

import io.swagger.v3.oas.annotations.media.Schema;

public record GradeResponse(
        @Schema(description = "직급 ID", example = "1") Long id,
        @Schema(description = "직급 이름", example = "대리") String name,
        @Schema(description = "직급 순서", example = "1") Integer order) {
    public static GradeResponse from(final Grade grade) {
        return new GradeResponse(grade.getId(), grade.getName(), grade.getOrder());
    }
}
