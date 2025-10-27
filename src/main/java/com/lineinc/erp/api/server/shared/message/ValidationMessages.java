package com.lineinc.erp.api.server.shared.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessages {

    // ===== 시스템 공통 메시지 =====
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";
    public static final String RESOURCE_NOT_FOUND = "해당 리소스를 찾을 수 없습니다.";
    public static final String DEFAULT_INVALID_INPUT = "입력값이 유효하지 않습니다.";
    public static final String FILE_PROCESS_ERROR = "파일 처리 중 오류가 발생했습니다.";
    public static final String INVALID_PROPERTY_REFERENCE = "요청에 잘못된 정렬 조건 또는 필드명이 포함되어 있습니다.";
    public static final String RATE_LIMIT_EXCEEDED = "요청 횟수 제한을 초과했습니다.";
    public static final String CHANGE_HISTORY_NOT_FOUND = "해당 변경이력을 찾을 수 없습니다.";
    public static final String MUST_HAVE_ONE_MAIN_CONTACT = "대표 담당자는 최소 1명 이상 등록해야 합니다.";
    public static final String INITIAL_CREATION = "최초 작성";

    // ===== 인증/권한 관련 메시지 =====
    public static final String ACCESS_DENIED = "접근 권한이 없습니다.";
    public static final String NO_MENU_PERMISSION = "해당 메뉴에 대한 권한이 없습니다.";
    public static final String ROLE_NOT_FOUND = "권한 그룹을 찾을 수 없습니다.";
    public static final String ROLE_NAME_ALREADY_EXISTS = "이미 존재하는 권한 그룹명입니다.";
    public static final String SOME_PERMISSIONS_NOT_FOUND = "일부 권한이 존재하지 않습니다.";

    // ===== 사용자/계정 관련 메시지 =====
    public static final String USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    public static final String USER_NOT_ACTIVE = "비활성화된 계정입니다.";
    public static final String LOGIN_ID_ALREADY_EXISTS = "이미 사용 중인 로그인 아이디 입니다.";
    public static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";
    public static final String PASSWORD_RESET = "비밀번호가 초기화되었습니다.";
    public static final String DEPARTMENT_NOT_FOUND = "존재하지 않는 부서입니다.";
    public static final String GRADE_NOT_FOUND = "존재하지 않는 직급입니다.";
    public static final String POSITION_NOT_FOUND = "존재하지 않는 직책입니다.";

    // ===== 발주처/현장 관련 메시지 =====
    public static final String CLIENT_COMPANY_NOT_FOUND = "존재하지 않는 발주처입니다.";
    public static final String BUSINESS_NUMBER_ALREADY_EXISTS = "이미 등록된 사업자등록번호입니다.";
    public static final String INVALID_BUSINESS_NUMBER = "유효한 사업자등록번호 형식이 아닙니다.";
    public static final String SITE_NOT_FOUND = "존재하지 않는 현장입니다.";
    public static final String SITE_NAME_ALREADY_EXISTS = "이미 존재하는 현장명입니다.";
    public static final String SITE_PROCESS_NOT_FOUND = "존재하지 않는 공정입니다.";
    public static final String SITE_PROCESS_NOT_MATCH_SITE = "해당 공정은 요청한 현장에 포함되어 있지 않습니다.";
    public static final String CLIENT_COMPANY_FILE_NOT_FOUND = "파일은 최소 1개 이상 등록해야 합니다.";

    // ===== 외주업체 관련 메시지 =====
    public static final String OUTSOURCING_COMPANY_NOT_FOUND = "외주업체를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND = "존재하지 않는 외주업체 계약입니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND = "기사를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND = "장비를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_SUB_EQUIPMENT_NOT_FOUND = "서브장비를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_WORKER_NOT_FOUND = "인력을 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_NOT_FOUND = "공사항목을 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_GROUP_NOT_FOUND = "공사항목 그룹을 찾을 수 없습니다.";

    // ===== 노무/인력 관련 메시지 =====
    public static final String LABOR_NOT_FOUND = "존재하지 않는 노무입니다.";
    public static final String LABOR_ALREADY_EXISTS = "이미 등록된 인력입니다. 동일한 주민등록번호의 인력이 존재합니다.";
    public static final String TEMPORARY_LABOR_NAME_REQUIRED = "임시 인력 등록시 이름은 필수입니다.";
    public static final String LABOR_PAYROLL_SUMMARY_NOT_FOUND = "존재하지 않는 노무명세서 집계입니다.";
    public static final String LABOR_PAYROLL_NOT_FOUND = "존재하지 않는 노무명세서입니다.";
    public static final String DAILY_REPORT_EMPLOYEE_DUPLICATE_LABOR_ID = "같은 인력이 중복으로 등록되었습니다.";
    public static final String DAILY_REPORT_DIRECT_CONTRACT_DUPLICATE_LABOR_ID = "같은 인력에 같은 단가로 중복 등록되었습니다.";

    // ===== 강재/자재 관련 메시지 =====
    public static final String STEEL_MANAGEMENT_NOT_FOUND = "강재 관리를 찾을 수 없습니다.";
    public static final String STEEL_MANAGEMENT_DETAIL_NOT_FOUND = "강재 관리 상세를 찾을 수 없습니다.";
    public static final String MATERIAL_MANAGEMENT_NOT_FOUND = "존재하지 않는 자재관리입니다.";
    public static final String CANNOT_APPROVE_RELEASED_STEEL = "이미 반출된 건은 승인으로 변경할 수 없습니다.";
    public static final String CANNOT_RELEASE_NON_APPROVED_STEEL = "반출 처리는 승인된 건만 가능합니다.";
    public static final String INVALID_INITIAL_STEEL_TYPE = "유효하지 않은 초기 강재 타입입니다.";
    public static final String CANNOT_DELETE_APPROVED_OR_RELEASED_STEEL = "승인 혹은 반출 상태인 강재수불부는 삭제가 불가능합니다.";
    public static final String APPROVAL_CREATION = "승인";
    public static final String RELEASE_CREATION = "반출";
    public static final String STEEL_MANAGEMENT_ALREADY_EXISTS = "해당 현장과 공정에 대한 강재수불부가 이미 존재합니다.";

    // ===== 관리비/유류 관련 메시지 =====
    public static final String MANAGEMENT_COST_NOT_FOUND = "존재하지 않는 관리비입니다.";
    public static final String FUEL_AGGREGATION_NOT_FOUND = "유류집계를 찾을 수 없습니다.";

    // ===== 출역일보 관련 메시지 =====
    public static final String DAILY_REPORT_NOT_FOUND = "출역일보를 찾을 수 없습니다.";
    public static final String DAILY_REPORT_ALREADY_EXISTS = "같은 날짜, 현장, 공정에 대한 출역일보가 이미 존재합니다.";
    public static final String DAILY_REPORT_EMPLOYEE_MUST_BE_REGULAR = "직원 출역 정보에는 정규직원만 추가할 수 있습니다.";
    public static final String DAILY_REPORT_DIRECT_CONTRACT_INVALID_TYPE = "직영/용역 출역 정보에는 직영/용역 또는 기타 인력만 추가할 수 있습니다.";
    public static final String DAILY_REPORT_LABOR_ALREADY_EXISTS = "해당 날짜에 이미 출근한 인력입니다.";
    public static final String DAILY_REPORT_CANNOT_EDIT_PAST_DATE = "당일까지만 출역일보를 수정할 수 있습니다.";
    public static final String DAILY_REPORT_EDIT_NOT_ALLOWED = "출역일보 수정 기한이 지났거나 권한이 없습니다.";

    // ===== 현장관리비 관련 메시지 =====
    public static final String SITE_MANAGEMENT_COST_ALREADY_EXISTS = "해당 년월, 현장, 공정에 대한 현장관리비 데이터가 이미 존재합니다.";

    // ===== 입력값 검증 관련 메시지 =====
    public static final String INVALID_PHONE = "유효한 휴대폰 번호 형식이 아닙니다.";
    public static final String INVALID_LANDLINE = "유효한 유선 전화번호 형식이 아닙니다.";
    public static final String INVALID_PHONE_OR_LANDLINE = "유효한 전화번호 형식(휴대폰 또는 유선전화)이 아닙니다.";
    public static final String INVALID_URL = "유효한 URL 형식이 아닙니다.";
    public static final String INVALID_DATE_FORMAT = "유효한 날짜 형식이 아닙니다. (YYYY-MM-DD 형식으로 입력해주세요)";
    public static final String INVALID_DOWNLOAD_FIELD = "다운로드할 수 없는 필드입니다: ";

    // ===== HTTP 관련 메시지 =====
    public static final String NOT_ACCEPTABLE = "요청한 Accept 타입에 대해 응답할 수 없습니다.";
    public static final String UNSUPPORTED_CONTENT_TYPE = "지원하지 않는 Content-Type입니다.";
}