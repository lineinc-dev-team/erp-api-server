package com.lineinc.erp.api.server.shared.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.lineinc.erp.api.server.shared.constant.AppConstants;

public final class DateTimeFormatUtils {

    private DateTimeFormatUtils() {
        // 인스턴스 생성 방지
    }

    public static final DateTimeFormatter DATE_FORMATTER_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static OffsetDateTime toOffsetDateTime(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().atOffset(AppConstants.KOREA_ZONE_OFFSET);
    }

    public static LocalDate toKoreaLocalDate(final OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.atZoneSameInstant(AppConstants.KOREA_ZONE_OFFSET).toLocalDate();
    }

    public static String formatKoreaLocalDate(final OffsetDateTime offsetDateTime) {
        final LocalDate localDate = toKoreaLocalDate(offsetDateTime);
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER_YMD.format(localDate);
    }

    public static String formatKoreaLocalDate(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER_YMD.format(localDate);
    }

    /**
     * 한국 날짜를 UTC 시작 시간으로 변환 (검색 시작용)
     * 예: 2024-12-31 -> 2024-12-30 15:00:00+00
     */
    public static OffsetDateTime toUtcStartOfDay(final LocalDate koreaLocalDate) {
        if (koreaLocalDate == null) {
            return null;
        }
        // 한국 시간 00:00:00을 UTC로 변환
        return koreaLocalDate.atStartOfDay().atOffset(AppConstants.KOREA_ZONE_OFFSET)
                .toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * 한국 날짜를 UTC 끝 시간으로 변환 (검색 종료용)
     * 예: 2024-12-31 -> 2025-01-01 14:59:59.999999999+00
     */
    public static OffsetDateTime toUtcEndOfDay(final LocalDate koreaLocalDate) {
        if (koreaLocalDate == null) {
            return null;
        }
        // 한국 시간 23:59:59.999999999을 UTC로 변환
        return koreaLocalDate.atTime(23, 59, 59, 999999999)
                .atOffset(AppConstants.KOREA_ZONE_OFFSET)
                .toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * 날짜 범위 검색을 위한 UTC 시작/종료 시간 쌍 반환
     * 
     * @return [시작시간, 종료시간] 배열
     */
    public static OffsetDateTime[] getUtcDateRange(final LocalDate koreaLocalDate) {
        if (koreaLocalDate == null) {
            return new OffsetDateTime[] { null, null };
        }
        return new OffsetDateTime[] {
                toUtcStartOfDay(koreaLocalDate),
                toUtcEndOfDay(koreaLocalDate)
        };
    }
}
