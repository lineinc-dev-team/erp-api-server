package com.lineinc.erp.api.server.shared.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.lineinc.erp.api.server.shared.constant.AppConstants;

public final class DateTimeFormatUtils {

    private DateTimeFormatUtils() {
        // 인스턴스 생성 방지
    }

    public static final DateTimeFormatter DATE_FORMATTER_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static OffsetDateTime toOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().atOffset(AppConstants.KOREA_ZONE_OFFSET);
    }

    public static LocalDate toKoreaLocalDate(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.atZoneSameInstant(AppConstants.KOREA_ZONE_OFFSET).toLocalDate();
    }

    public static String formatKoreaLocalDate(OffsetDateTime offsetDateTime) {
        LocalDate localDate = toKoreaLocalDate(offsetDateTime);
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER_YMD.format(localDate);
    }
}

