package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 등록 응답")
public record DailyReportCreateResponse(
        @Schema(description = "출역일보 ID", example = "1") Long id,
        
        @Schema(description = "등록 성공 여부", example = "true") Boolean success,
        
        @Schema(description = "메시지", example = "출역일보가 성공적으로 등록되었습니다.") String message
) {}
