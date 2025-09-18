package com.lineinc.erp.api.server.shared.excel;

import static com.lineinc.erp.api.server.shared.constant.AppConstants.EMPTY_VALUE;

import java.util.Map;
import java.util.Objects;

import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.FormatUtils;
import com.lineinc.erp.api.server.shared.util.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * 발주처 Excel 다운로드 설정
 * 헤더 매핑 정보만 담당 (비즈니스 로직 없음)
 */
@UtilityClass
public final class ClientCompanyExcelConfig {

    // Excel 헤더 매핑 정보
    public static final Map<String, String> HEADERS = Map.ofEntries(
            Map.entry("id", "No."),
            Map.entry("businessNumber", "사업자등록번호"),
            Map.entry("name", "발주처명"),
            Map.entry("email", "이메일"),
            Map.entry("ceoName", "대표자명"),
            Map.entry("address", "본사 주소"),
            Map.entry("phoneNumber", "개인 휴대폰"),
            Map.entry("landlineNumber", "전화번호"),
            Map.entry("contactName", "담당자명"),
            Map.entry("contactPositionAndDepartment", "부서/직급"),
            Map.entry("contactLandlineNumberAndEmail", "담당자 연락처/이메일"),
            Map.entry("userName", "본사담당자명"),
            Map.entry("isActive", "사용여부"),
            Map.entry("createdAtAndUpdatedAt", "등록일/수정일"),
            Map.entry("hasFile", "첨부파일 유무"),
            Map.entry("memo", "비고"));

    /**
     * 필드명을 Excel 헤더(한글)로 변환
     */
    public static String getHeaderName(final String field) {
        return HEADERS.get(field);
    }

    /**
     * 발주처 데이터를 Excel 셀 값으로 변환
     */
    public static String getCellValue(final ClientCompanyResponse company, final String field) {
        final var mainContact = company.contacts().stream()
                .findFirst().orElse(null);

        return switch (field) {
            case "businessNumber" -> company.businessNumber();
            case "name" -> company.name();
            case "email" -> company.email();
            case "ceoName" -> company.ceoName();
            case "address" -> StringUtils.joinWithSpace(company.address(), company.detailAddress());
            case "phoneNumber" -> company.phoneNumber();
            case "landlineNumber" -> company.landlineNumber();
            case "contactName" -> Objects.nonNull(mainContact) ? mainContact.name() : EMPTY_VALUE;
            case "contactPositionAndDepartment" ->
                Objects.nonNull(mainContact)
                        ? StringUtils.joinWithSlash(mainContact.position(), mainContact.department())
                        : EMPTY_VALUE;
            case "contactLandlineNumberAndEmail" ->
                Objects.nonNull(mainContact) ? StringUtils.joinWithSlash(mainContact.phoneNumber(), mainContact.email())
                        : EMPTY_VALUE;
            case "userName" -> Objects.nonNull(company.user()) ? company.user().username() : EMPTY_VALUE;
            case "isActive" -> FormatUtils.toYesNo(company.isActive());
            case "createdAtAndUpdatedAt" ->
                StringUtils.joinWithSlash(
                        DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt()),
                        DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt()));
            case "hasFile" -> FormatUtils.toYesNo(company.hasFile());
            case "memo" -> company.memo();
            default -> null;
        };
    }
}
