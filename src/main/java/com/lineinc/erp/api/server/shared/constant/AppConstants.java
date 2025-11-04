package com.lineinc.erp.api.server.shared.constant;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * 애플리케이션 전역 상수 정의
 */
@UtilityClass
public final class AppConstants {

    // ==================== 인증 및 권한 ====================
    /**
     * 권한 그룹명
     */
    public static final String ROLE_ADMIN_NAME = "전체권한";

    /**
     * 관리자 계정 정보
     */
    public static final String ADMIN_LOGIN_ID = "admin";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_USERNAME = "관리자";

    /**
     * 시스템 계정명
     */
    public static final String SYSTEM_NAME = "system";

    // ==================== 회사 정보 ====================
    /**
     * 회사명
     */
    public static final String LINE_INC_NAME = "라인공영";

    // ==================== 메뉴 관리 ====================
    /**
     * 시스템 메뉴 이름
     */
    public static final String MENU_AGGREGATION_MANAGEMENT = "집계 관리";
    public static final String MENU_ACCOUNT = "계정 관리";
    public static final String MENU_PERMISSION = "권한 관리";
    public static final String MENU_CLIENT_COMPANY = "발주처 관리";
    public static final String MENU_SITE = "현장 관리";
    public static final String MENU_SITE_MANAGEMENT_COST = "현장/본사 관리비 관리";
    public static final String MENU_MANAGEMENT_COST = "관리비 관리";
    public static final String MENU_STEEL_MANAGEMENT = "강재수불부 관리";
    public static final String MENU_MATERIAL_MANAGEMENT = "자재 관리";
    public static final String MENU_FUEL_AGGREGATION = "유류집계 관리";
    public static final String MENU_LABOR_MANAGEMENT = "노무 관리";
    public static final String MENU_LABOR_PAYROLL = "노무명세서 관리";
    public static final String MENU_OUTSOURCING_COMPANY = "외주업체 관리";
    public static final String MENU_OUTSOURCING_COMPANY_CONTRACT = "외주업체 계약 관리";
    public static final String MENU_WORK_DAILY_REPORT = "출역일보";

    /**
     * 시스템 메뉴 이름 목록 (표시 순서)
     */
    public static final List<String> MENU_NAMES = List.of(
            MENU_AGGREGATION_MANAGEMENT, // 1. 집계 관리
            MENU_CLIENT_COMPANY, // 2. 발주처 관리
            MENU_SITE, // 3. 현장 관리
            MENU_MATERIAL_MANAGEMENT, // 4. 자재 관리
            MENU_STEEL_MANAGEMENT, // 5. 강재수불부 관리
            MENU_FUEL_AGGREGATION, // 6. 유류집계 관리
            MENU_LABOR_MANAGEMENT, // 7. 노무 관리
            MENU_LABOR_PAYROLL, // 8. 노무명세서 관리
            MENU_OUTSOURCING_COMPANY, // 9. 외주업체 관리
            MENU_OUTSOURCING_COMPANY_CONTRACT, // 10. 외주업체 계약 관리
            MENU_SITE_MANAGEMENT_COST, // 11. 현장/본사 관리비 관리
            MENU_MANAGEMENT_COST, // 12. 관리비 관리
            MENU_WORK_DAILY_REPORT, // 13. 출역일보
            MENU_ACCOUNT, // 14. 계정 관리
            MENU_PERMISSION // 15. 권한 관리
    );

    // ==================== 조직 관리 ====================
    /**
     * 직급(Grade) 상수
     */
    public static final String GRADE_CEO = "대표이사";
    public static final String GRADE_VICE_CHAIRMAN = "부회장";
    public static final String GRADE_EXECUTIVE_VICE_PRESIDENT = "전무";
    public static final String GRADE_MANAGING_DIRECTOR = "상무";
    public static final String GRADE_EXECUTIVE_DIRECTOR = "이사";
    public static final String GRADE_GENERAL_MANAGER = "부장";
    public static final String GRADE_DEPUTY_GENERAL_MANAGER = "차장";
    public static final String GRADE_MANAGER = "과장";
    public static final String GRADE_ASSISTANT_MANAGER = "대리";
    public static final String GRADE_SUPERVISOR = "주임";
    public static final String GRADE_EMPLOYEE = "사원";
    public static final String GRADE_FOREMAN = "반장";
    public static final String GRADE_TECHNICIAN = "기사";

    /**
     * 직책(Position) 상수
     */
    public static final String POSITION_TEAM_LEADER = "팀장";
    public static final String POSITION_PART_LEADER = "파트장";
    public static final String POSITION_HEAD = "실장";

    /**
     * 부서(Department) 상수
     */
    public static final String DEPT_CEO = "대표이사";
    public static final String DEPT_LEGAL_AUDIT = "법무감사실";
    public static final String DEPT_MANAGEMENT = "관리부";
    public static final String DEPT_MATERIAL_PURCHASING = "자재 외주구매";
    public static final String DEPT_ENGINEERING = "공무부";
    public static final String DEPT_CIVIL_ENGINEERING = "토목공사팀";
    public static final String DEPT_DUSON_INTERNATIONAL = "두손인터내셔널";

    /**
     * 직급 목록 (계급 순서)
     */
    public static final List<String> GRADE_NAMES = List.of(
            GRADE_CEO,
            GRADE_VICE_CHAIRMAN,
            GRADE_EXECUTIVE_VICE_PRESIDENT,
            GRADE_MANAGING_DIRECTOR,
            GRADE_EXECUTIVE_DIRECTOR,
            GRADE_GENERAL_MANAGER,
            GRADE_DEPUTY_GENERAL_MANAGER,
            GRADE_MANAGER,
            GRADE_ASSISTANT_MANAGER,
            GRADE_SUPERVISOR,
            GRADE_EMPLOYEE,
            GRADE_FOREMAN,
            GRADE_TECHNICIAN);

    /**
     * 직책 목록
     */
    public static final List<String> POSITION_NAMES = List.of(
            POSITION_TEAM_LEADER,
            POSITION_PART_LEADER,
            POSITION_HEAD);

    /**
     * 부서 목록
     */
    public static final List<String> DEPARTMENT_NAMES = List.of(
            DEPT_CEO,
            DEPT_LEGAL_AUDIT,
            DEPT_MANAGEMENT,
            DEPT_MATERIAL_PURCHASING,
            DEPT_ENGINEERING,
            DEPT_CIVIL_ENGINEERING,
            DEPT_DUSON_INTERNATIONAL);

    // ==================== 시간 및 지역 설정 ====================
    /**
     * 한국 시간대 설정
     */
    public static final ZoneOffset KOREA_ZONE_OFFSET = ZoneOffset.ofHours(9);
    public static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    // ==================== 시스템 설정 ====================
    /**
     * 세션 타임아웃 (초 단위, 1800초 = 30분)
     */
    public static final int DEFAULT_SESSION_TIMEOUT_SECONDS = 1800;

    /**
     * JPA 시퀀스 AllocationSize 기본값
     */
    public static final int SEQUENCE_ALLOCATION_DEFAULT = 1;

    // ==================== JPA 엔티티 컬럼명 ====================
    /**
     * 외래키 컬럼명
     */
    public static final String CLIENT_COMPANY_ID = "client_company_id";
    public static final String USER_ID = "user_id";
    public static final String UPDATED_BY_USER_ID = "updated_by_id";
    public static final String SITE_ID = "site_id";
    public static final String SITE_PROCESS_ID = "site_process_id";
    public static final String DAILY_REPORT_ID = "daily_report_id";
    public static final String OUTSOURCING_COMPANY_ID = "outsourcing_company_id";
    public static final String LABOR_ID = "labor_id";
    public static final String FUEL_AGGREGATION_ID = "fuel_aggregation_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_WORKER_ID = "outsourcing_company_contract_worker_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_DRIVER_ID = "outsourcing_company_contract_driver_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_ID = "outsourcing_company_contract_equipment_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_ID = "outsourcing_company_contract_construction_id";
    public static final String DAILY_REPORT_OUTSOURCING_CONSTRUCTION_GROUP_ID = "daily_report_outsourcing_construction_group_id";
    public static final String DAILY_REPORT_OUTSOURCING_COMPANY_ID = "daily_report_outsourcing_company_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_GROUP_ID = "outsourcing_company_contract_construction_group_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_ID = "outsourcing_company_contract_id";
    public static final String FUEL_INFO_ID = "fuel_info_id";
    public static final String OUTSOURCING_COMPANY_CONTRACT_SUB_EQUIPMENT_ID = "outsourcing_company_contract_sub_equipment_id";
    public static final String DEPARTMENT_ID = "department_id";
    public static final String GRADE_ID = "grade_id";
    public static final String POSITION_ID = "position_id";
    public static final String ROLE_ID = "role_id";
    public static final String STEEL_MANAGEMENT_V2_ID = "steel_management_v2_id";
    public static final String SITE_MANAGEMENT_COST_ID = "site_management_cost_id";

    // ==================== JPA 엔티티 매핑 속성명 ====================
    /**
     * @OneToMany mappedBy 속성값
     */
    public static final String CLIENT_COMPANY_MAPPED_BY = "clientCompany";
    public static final String DAILY_REPORT_MAPPED_BY = "dailyReport";
    public static final String FUEL_AGGREGATION_MAPPED_BY = "fuelAggregation";
    public static final String LABOR_MAPPED_BY = "labor";
    public static final String DAILY_REPORT_OUTSOURCING_COMPANY_MAPPED_BY = "dailyReportOutsourcingCompany";
    public static final String DAILY_REPORT_OUTSOURCING_CONSTRUCTION_GROUP_MAPPED_BY = "dailyReportOutsourcingConstructionGroup";
    public static final String USER_MAPPED_BY = "user";
    public static final String STEEL_MANAGEMENT_V2_MAPPED_BY = "steelManagementV2";

    // ==================== 기타 상수 ====================
    /**
     * 빈 값
     */
    public static final String EMPTY_VALUE = "";
}
