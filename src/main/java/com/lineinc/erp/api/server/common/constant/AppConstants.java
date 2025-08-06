package com.lineinc.erp.api.server.common.constant;

import java.util.List;
import java.time.ZoneOffset;

public final class AppConstants {

    // 권한 그룹명 관련 상수
    public static final String ROLE_ADMIN_NAME = "전체권한";

    // 관리자 계정 관련 상수
    public static final String ADMIN_LOGIN_ID = "admin";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_USERNAME = "관리자";

    // 시스템 관련 상수
    public static final String SYSTEM_NAME = "system";

    // 시스템 기본 메뉴 이름 관련 상수
    public static final String MENU_ACCOUNT = "계정 관리";
    public static final String MENU_PERMISSION = "권한 관리";
    public static final String MENU_CLIENT_COMPANY = "발주처 관리";
    public static final String MENU_SITE = "현장 관리";
    public static final String MENU_MANAGEMENT_COST = "관리비 관리";
    public static final String MENU_STEEL_MANAGEMENT = "강재 관리";
    public static final String MENU_MATERIAL_MANAGEMENT = "자재 관리";
    public static final String MENU_OUTSOURCING_COMPANY = "외주업체 관리";

    // 시스템 기본 메뉴 이름 목록
    public static final List<String> MENU_NAMES = List.of(
            MENU_ACCOUNT,
            MENU_PERMISSION,
            MENU_CLIENT_COMPANY,
            MENU_SITE,
            MENU_MANAGEMENT_COST,
            MENU_STEEL_MANAGEMENT,
            MENU_MATERIAL_MANAGEMENT,
            MENU_OUTSOURCING_COMPANY
    );

    // 직급(Grade) 관련 상수
    public static final String GRADE_EMPLOYEE = "사원";
    public static final String GRADE_ASSISTANT_MANAGER = "대리";
    public static final String GRADE_MANAGER = "과장";

    // 직책(Position) 관련 상수
    public static final String POSITION_TEAM_LEADER = "팀장";
    public static final String POSITION_PART_LEADER = "파트장";
    public static final String POSITION_HEAD = "실장";

    // 부서(Department) 관련 상수
    public static final String DEPT_SUPPORT = "경영지원팀";
    public static final String DEPT_DEVELOPMENT = "개발팀";
    public static final String DEPT_SALES = "영업팀";

    // 직급 전체 목록
    public static final List<String> GRADE_NAMES = List.of(
            GRADE_EMPLOYEE,
            GRADE_ASSISTANT_MANAGER,
            GRADE_MANAGER
    );

    // 직책 전체 목록
    public static final List<String> POSITION_NAMES = List.of(
            POSITION_TEAM_LEADER,
            POSITION_PART_LEADER,
            POSITION_HEAD
    );

    // 부서 전체 목록
    public static final List<String> DEPARTMENT_NAMES = List.of(
            DEPT_SUPPORT,
            DEPT_DEVELOPMENT,
            DEPT_SALES
    );

    // 시간 관련 상수
    public static final ZoneOffset KOREA_ZONE_OFFSET = ZoneOffset.ofHours(9);

    // 세션 타임아웃 기본값 (초 단위, 예: 1800초 = 30분)
    public static final int DEFAULT_SESSION_TIMEOUT_SECONDS = 1800;

    private AppConstants() {
    } // 인스턴스화 방지
}