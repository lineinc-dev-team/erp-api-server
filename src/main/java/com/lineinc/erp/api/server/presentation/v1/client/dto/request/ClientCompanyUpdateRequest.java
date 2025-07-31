package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import com.lineinc.erp.api.server.common.validation.MultiConstraint;
import com.lineinc.erp.api.server.common.validation.ValidatorType;
import com.lineinc.erp.api.server.domain.client.enums.PaymentMethod;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UpdateUserRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import java.util.List;

public record ClientCompanyUpdateRequest(
        @Schema(description = "발주처명", example = "삼성건설")
        String name,

        @MultiConstraint(type = ValidatorType.BUSINESS_NUMBER)
        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자명", example = "홍길동")
        String ceoName,

        @Schema(description = "주소", example = "서울시 강남구")
        String address,

        @Schema(description = "상세 주소", example = "강남대로 123, 3층")
        String detailAddress,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER)
        @Schema(description = "담당자 유선 전화번호", example = "02-123-5678")
        String landlineNumber,

        @MultiConstraint(type = ValidatorType.PHONE)
        @Schema(description = "담당자 연락처", example = "010-1234-5678")
        String phoneNumber,

        @Email
        @Schema(description = "이메일", example = "example@samsung.com")
        String email,

        @Schema(description = "결제 방식", example = "CASH")
        PaymentMethod paymentMethod,

        @Schema(description = "결제 유예 기간", example = "2")
        String paymentPeriod,

        @Schema(description = "비고 / 메모")
        String memo,

        @Schema(description = "사용 여부", example = "true")
        Boolean isActive,

        @Schema(description = "본사 담당자 ID", example = "123")
        Long userId,

        @Valid
        @Schema(description = "담당자 목록")
        List<ClientCompanyContactUpdateRequest> contacts,

        @Valid
        @Schema(description = "파일 목록")
        List<ClientCompanyFileUpdateRequest> files,

        @Schema(description = "수정 이력 리스트")
        List<ClientCompanyUpdateRequest.ChangeHistoryRequest> changeHistories
) {
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1")
            Long id,

            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트")
            String memo
    ) {
    }
}
