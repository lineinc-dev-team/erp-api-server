package com.lineinc.erp.api.server.shared.message;

public final class ValidationMessages {

    private ValidationMessages() {
    } // 인스턴스화 방지

    // ===== 시스템 공통 메시지 =====
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";
    public static final String RESOURCE_NOT_FOUND = "해당 리소스를 찾을 수 없습니다.";
    public static final String DEFAULT_INVALID_INPUT = "입력값이 유효하지 않습니다.";
    public static final String FILE_PROCESS_ERROR = "파일 처리 중 오류가 발생했습니다.";
    public static final String INVALID_PROPERTY_REFERENCE = "요청에 잘못된 정렬 조건 또는 필드명이 포함되어 있습니다.";
    public static final String RATE_LIMIT_EXCEEDED = "요청 횟수 제한을 초과했습니다.";

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

    // ===== 발주처/현장 관련 메시지 =====
    public static final String CLIENT_COMPANY_NOT_FOUND = "존재하지 않는 발주처입니다.";
    public static final String BUSINESS_NUMBER_ALREADY_EXISTS = "이미 등록된 사업자등록번호입니다.";
    public static final String INVALID_BUSINESS_NUMBER = "유효한 사업자등록번호 형식이 아닙니다.";
    public static final String SITE_NOT_FOUND = "존재하지 않는 현장입니다.";
    public static final String SITE_NAME_ALREADY_EXISTS = "이미 존재하는 현장명입니다.";
    public static final String SITE_PROCESS_NOT_FOUND = "존재하지 않는 공정입니다.";
    public static final String SITE_PROCESS_NOT_MATCH_SITE = "해당 공정은 요청한 현장에 포함되어 있지 않습니다.";

    // ===== 외주업체 관련 메시지 =====
    public static final String OUTSOURCING_COMPANY_NOT_FOUND = "외주업체를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND = "존재하지 않는 외주업체 계약입니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND = "기사를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND = "장비를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_SUB_EQUIPMENT_NOT_FOUND = "서브장비를 찾을 수 없습니다.";
    public static final String OUTSOURCING_COMPANY_CONTRACT_WORKER_NOT_FOUND = "인력을 찾을 수 없습니다.";

    // ===== 노무/인력 관련 메시지 =====
    public static final String LABOR_NOT_FOUND = "존재하지 않는 노무입니다.";
    public static final String LABOR_ALREADY_EXISTS = "이미 등록된 인력입니다. 이름과 주민등록번호가 동일한 인력이 존재합니다.";
    public static final String TEMPORARY_LABOR_NAME_REQUIRED = "임시 인력 등록시 이름은 필수입니다.";
    public static final String MUST_HAVE_ONE_MAIN_CONTACT = "대표 담당자는 반드시 한 명이어야 합니다.";

    // ===== 강재/자재 관련 메시지 =====
    public static final String STEEL_MANAGEMENT_NOT_FOUND = "강재 관리를 찾을 수 없습니다.";
    public static final String MATERIAL_MANAGEMENT_NOT_FOUND = "존재하지 않는 자재관리입니다.";
    public static final String CANNOT_APPROVE_RELEASED_STEEL = "이미 반출된 건은 승인으로 변경할 수 없습니다.";
    public static final String CANNOT_RELEASE_NON_APPROVED_STEEL = "반출 처리는 승인된 건만 가능합니다.";
    public static final String INVALID_INITIAL_STEEL_TYPE = "유효하지 않은 초기 강재 타입입니다.";

    // ===== 관리비/유류 관련 메시지 =====
    public static final String MANAGEMENT_COST_NOT_FOUND = "존재하지 않는 관리비입니다.";
    public static final String FUEL_AGGREGATION_NOT_FOUND = "유류집계를 찾을 수 없습니다.";

    // ===== 출역일보 관련 메시지 =====
    public static final String DAILY_REPORT_NOT_FOUND = "출역일보를 찾을 수 없습니다.";
    public static final String DAILY_REPORT_ALREADY_EXISTS = "같은 날짜, 현장, 공정에 대한 출역일보가 이미 존재합니다.";

    // ===== 입력값 검증 관련 메시지 =====
    public static final String INVALID_PHONE = "유효한 휴대폰 번호 형식이 아닙니다.";
    public static final String INVALID_LANDLINE = "유효한 유선 전화번호 형식이 아닙니다.";
    public static final String INVALID_PHONE_OR_LANDLINE = "유효한 전화번호 형식(휴대폰 또는 유선전화)이 아닙니다.";
    public static final String INVALID_URL = "유효한 URL 형식이 아닙니다.";
    public static final String INVALID_DOWNLOAD_FIELD = "다운로드할 수 없는 필드입니다: ";

    // ===== HTTP 관련 메시지 =====
    public static final String NOT_ACCEPTABLE = "요청한 Accept 타입에 대해 응답할 수 없습니다.";
    public static final String UNSUPPORTED_CONTENT_TYPE = "지원하지 않는 Content-Type입니다.";
}