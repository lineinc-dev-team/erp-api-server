package com.lineinc.erp.api.server.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BankType {
    // 한국어 라벨 가나다순 정렬 (ㄱ -> ㅎ)

    // ㄱ (G, K)
    KOREA_SAVINGS_BANK("고려저축은행"),
    KWANGJU_BANK("광주은행"),
    KYOBO_SECURITIES("교보증권"),
    KOOKMIN_BANK("국민은행"),
    KYONGNAM_BANK("경남은행"),
    INDUSTRIAL_BANK("기업은행"),

    // ㄴ (N)
    NONGHYUP_BANK("농협은행"),

    // ㄷ (D)
    DAOL_INVESTMENT("다올투자증권"),
    DAEGU_BANK("대구은행"),
    DAEMYUNG_SAVINGS_BANK("대명저축은행"),
    DAISHIN_SAVINGS_BANK("대신저축은행"),
    DAISHIN_SECURITIES("대신증권"),

    // ㅁ (M)
    MERITZ_SECURITIES("메리츠증권"),
    MIRAE_ASSET_SECURITIES("미래에셋증권"),

    // ㅂ (B)
    BOOKOOK_SECURITIES("부국증권"),
    BUSAN_BANK("부산은행"),

    // ㅅ (S, Sh)
    KDB_BANK("산업은행"), // 구 KDB산업은행
    FOREST_COOP_BANK("산림조합은행"),
    SAMSUNG_SECURITIES("삼성증권"),
    SANGSANGIN_SAVINGS_BANK("상상인저축은행"),
    SAEMAUL_GEUMGO("새마을금고"),
    SUHYUP_BANK("수협은행"),
    SMART_SAVINGS_BANK("스마트저축은행"),
    SHINYOUNG_SECURITIES("신영증권"),
    SHINHAN_BANK("신한은행"),
    SHINHAN_FINANCIAL_INVESTMENT("신한금융투자"),
    SHINHAN_SAVINGS_BANK("신한저축은행"),

    // ㅇ (A, E, I, O, U, W, Y)
    IM_INVESTMENT_SECURITIES("아이엠투자증권"),
    ACUON_SAVINGS_BANK("애큐온저축은행"),
    YEGARAM_SAVINGS_BANK("예가람저축은행"),
    WOORI_BANK("우리은행"),
    POST_OFFICE("우체국"),
    WELCOME_SAVINGS_BANK("웰컴저축은행"),
    YUANTA_SAVINGS_BANK("유안타저축은행"),
    YUANTA_SECURITIES("유안타증권"),
    EUGENE_INVESTMENT("유진투자증권"),
    EBEST_INVESTMENT("이베스트투자증권"),

    // ㅈ (J)
    JEJU_BANK("제주은행"),
    JEONBUK_BANK("전북은행"),

    // ㅋ (K)
    KAKAO_BANK("카카오뱅크"),
    KAKAOPAY_SECURITIES("카카오페이증권"),
    CAPSTONE_INVESTMENT("캡스톤투자증권"),
    K_BANK("케이뱅크"),
    KF_INVESTMENT("케이에프투자증권"),
    KIWOOM_SECURITIES("키움증권"),

    // ㅌ (T)
    TOSS_BANK("토스뱅크"),
    TOSS_SECURITIES("토스증권"),

    // ㅍ (P)
    PEPPER_SAVINGS_BANK("페퍼저축은행"),

    // ㅎ (H)
    HANA_SAVINGS_BANK("하나저축은행"),
    HANA_FINANCIAL_INVESTMENT("하나금융투자"),
    HANA_BANK("하나은행"),
    HANA_SECURITIES("하나증권"),
    HANWHA_SECURITIES("한화증권"),
    KOREA_EXIMBANK("한국수출입은행"),
    KOREA_INVESTMENT_SAVINGS_BANK("한국투자저축은행"),
    KOREA_INVESTMENT("한국투자증권"),
    HYUNDAI_MOTOR_SECURITIES("현대차증권"),

    // 영문 상수명 알파벳순 정렬 (A -> Z)
    BNK_INVESTMENT("BNK투자증권"),
    DB_FINANCIAL_INVESTMENT("DB금융투자"),
    IBK_SECURITIES("IBK투자증권"),
    IM_BANK("IM뱅크"),
    JT_CHINAI_SAVINGS_BANK("JT친애저축은행"),
    JT_SAVINGS_BANK("JT저축은행"),
    KB_INVESTMENT("KB투자증권"),
    KB_SAVINGS_BANK("KB저축은행"),
    KB_SECURITIES("KB증권"),
    KTB_INVESTMENT("KTB투자증권"),
    NH_INVESTMENT("NH투자증권"),
    OK_SAVINGS_BANK("OK저축은행"),
    SBI_SAVINGS_BANK("SBI저축은행"),
    SK_SECURITIES("SK증권");

    private final String label;
}
