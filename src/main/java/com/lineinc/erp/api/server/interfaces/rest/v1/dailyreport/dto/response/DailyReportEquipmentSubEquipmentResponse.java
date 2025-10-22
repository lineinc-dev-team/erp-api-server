package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipmentSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractSubEquipmentResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주업체 서브 장비 응답")
public record DailyReportEquipmentSubEquipmentResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "작업내용", example = "기초 굴착 작업") String workContent,
        @Schema(description = "단가", example = "50000") Long unitPrice,
        @Schema(description = "작업시간", example = "8.0") Double workHours,
        @Schema(description = "비고", example = "정상 작동") String memo,
        @Schema(description = "서브 장비 정보") ContractSubEquipmentResponse subEquipment,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportEquipmentSubEquipmentResponse from(
            final DailyReportOutsourcingEquipmentSubEquipment subEquipment) {
        if (subEquipment == null) {
            return null;
        }

        // 서브 장비 정보가 삭제되었거나 존재하지 않는 경우 안전하게 처리
        ContractSubEquipmentResponse subEquipmentResponse = null;
        try {
            subEquipmentResponse = ContractSubEquipmentResponse
                    .from(subEquipment.getOutsourcingCompanyContractSubEquipment());
        } catch (final Exception e) {
            // 서브 장비가 삭제되었거나 존재하지 않는 경우 null로 처리
            subEquipmentResponse = null;
        }

        return new DailyReportEquipmentSubEquipmentResponse(
                subEquipment.getId(),
                subEquipment.getWorkContent(),
                subEquipment.getUnitPrice(),
                subEquipment.getWorkHours(),
                subEquipment.getMemo(),
                subEquipmentResponse,
                subEquipment.getCreatedAt(),
                subEquipment.getUpdatedAt());
    }

    public static List<DailyReportEquipmentSubEquipmentResponse> from(
            final List<DailyReportOutsourcingEquipmentSubEquipment> subEquipments) {
        return subEquipments.stream()
                .map(DailyReportEquipmentSubEquipmentResponse::from)
                .toList();
    }
}