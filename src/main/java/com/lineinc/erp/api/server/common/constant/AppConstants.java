package com.lineinc.erp.api.server.common.constant;

import java.util.List;

public final class AppConstants {
    public static final String COMPANY_MAIN_NAME = "라인공영";
    public static final String ROLE_MASTER_NAME = "전체권한";
    public static final String ROLE_SUB_MASTER_NAME = "전체권한(삭제 제외)";
    public static final String ROLE_SUB_MASTER_WITHOUT_PERMISSION_MENU = "전체권한(삭제/권한관리 제외)";
    public static final String ADMIN_LOGIN_ID = "admin";
    public static final String SYSTEM_NAME = "system";

    // 시스템 기본 메뉴 이름 상수
    public static final String MENU_ACCOUNT = "계정관리";
    public static final String MENU_OUTSOURCING_SETTLEMENT = "외주정산";
    public static final String MENU_CONTRACT = "계약/증빙";
    public static final String MENU_MATERIAL = "자재관리";
    public static final String MENU_OUTSOURCING = "외주관리";
    public static final String MENU_HR = "노무관리";
    public static final String MENU_EQUIPMENT = "장비관리";
    public static final String MENU_TAX = "세금계산서";
    public static final String MENU_REPORT = "통계/리포트";
    public static final String MENU_PERMISSION = "권한관리";

    // 시스템 기본 메뉴 이름 목록
    public static final List<String> MENU_NAMES = List.of(
            MENU_ACCOUNT,
            MENU_OUTSOURCING_SETTLEMENT,
            MENU_CONTRACT,
            MENU_MATERIAL,
            MENU_OUTSOURCING,
            MENU_HR,
            MENU_EQUIPMENT,
            MENU_TAX,
            MENU_REPORT,
            MENU_PERMISSION

    );

    private AppConstants() {
    } // 인스턴스화 방지
}