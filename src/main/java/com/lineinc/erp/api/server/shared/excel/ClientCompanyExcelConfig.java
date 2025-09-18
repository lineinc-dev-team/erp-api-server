package com.lineinc.erp.api.server.shared.excel;

import java.util.Map;

import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

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
                .filter(c -> c.isMain())
                .findFirst()
                .orElse(null);

        return switch (field) {
            case "businessNumber" -> company.businessNumber();
            case "name" -> company.name();
            case "email" -> company.email();
            case "ceoName" -> company.ceoName();
            case "address" -> company.address() + " " + company.detailAddress();
            case "phoneNumber" -> company.phoneNumber();
            case "landlineNumber" -> company.landlineNumber();
            case "contactName" -> mainContact != null ? mainContact.name() : "";
            case "contactPositionAndDepartment" ->
                mainContact != null ? mainContact.position() + " / " + mainContact.department() : "";
            case "contactLandlineNumberAndEmail" ->
                mainContact != null ? mainContact.phoneNumber() + " / " + mainContact.email() : "";
            case "userName" -> company.user() != null ? company.user().username() : "";
            case "isActive" -> company.isActive() ? "Y" : "N";
            case "createdAtAndUpdatedAt" ->
                DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt()) + "/"
                        + DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt());
            case "hasFile" -> company.hasFile() ? "Y" : "N";
            case "memo" -> company.memo();
            default -> null;
        };
    }
}
