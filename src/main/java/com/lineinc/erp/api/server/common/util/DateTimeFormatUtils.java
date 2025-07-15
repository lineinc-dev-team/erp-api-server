package com.lineinc.erp.api.server.common.util;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatUtils {

    private DateTimeFormatUtils() {
        // 인스턴스 생성 방지
    }

    public static final DateTimeFormatter DATE_FORMATTER_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_YMD_HM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}