package com.lineinc.erp.api.server.shared.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.experimental.UtilityClass;

/**
 * 날짜 및 시간 변환 유틸리티 클래스
 * 한국 시간대(UTC+9)와 UTC 간의 변환 및 포맷팅 기능 제공
 */
@UtilityClass
public class DateTimeFormatUtils {

    /**
     * 날짜 포맷터 (yyyy-MM-dd 형식)
     */
    public static final DateTimeFormatter DATE_FORMATTER_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * LocalDate를 한국 시간대 기준 OffsetDateTime으로 변환
     * 
     * @param localDate 변환할 날짜 (null 가능)
     * @return 한국 시간대 00:00:00의 OffsetDateTime (입력이 null이면 null 반환)
     */
    public static OffsetDateTime toOffsetDateTime(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().atOffset(AppConstants.KOREA_ZONE_OFFSET);
    }

    /**
     * OffsetDateTime을 한국 시간대의 LocalDate로 변환
     * 
     * @param offsetDateTime 변환할 날짜시간 (null 가능)
     * @return 한국 시간대 기준 LocalDate (입력이 null이면 null 반환)
     */
    public static LocalDate toKoreaLocalDate(final OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.atZoneSameInstant(AppConstants.KOREA_ZONE_OFFSET).toLocalDate();
    }

    /**
     * OffsetDateTime을 한국 날짜 문자열(yyyy-MM-dd)로 포맷
     * 
     * @param offsetDateTime 포맷할 날짜시간 (null 가능)
     * @return "yyyy-MM-dd" 형식의 문자열 (입력이 null이면 null 반환)
     */
    public static String formatKoreaLocalDate(final OffsetDateTime offsetDateTime) {
        final LocalDate localDate = toKoreaLocalDate(offsetDateTime);
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER_YMD.format(localDate);
    }

    /**
     * LocalDate를 날짜 문자열(yyyy-MM-dd)로 포맷
     * 
     * @param localDate 포맷할 날짜 (null 가능)
     * @return "yyyy-MM-dd" 형식의 문자열 (입력이 null이면 null 반환)
     */
    public static String formatKoreaLocalDate(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER_YMD.format(localDate);
    }

    /**
     * 한국 날짜를 UTC 시작 시간으로 변환 (검색 시작용)
     * 한국 시간 00:00:00을 UTC로 변환하여 해당 날짜의 시작 시점을 반환
     * 
     * @param koreaLocalDate 변환할 한국 날짜 (null 가능)
     * @return UTC 기준 해당 날짜의 시작 시간 (입력이 null이면 null 반환)
     *         예: 2024-12-31 -> 2024-12-30 15:00:00+00
     */
    public static OffsetDateTime toUtcStartOfDay(final LocalDate koreaLocalDate) {
        if (koreaLocalDate == null) {
            return null;
        }
        return koreaLocalDate.atStartOfDay().atOffset(AppConstants.KOREA_ZONE_OFFSET)
                .toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * 한국 날짜를 UTC 끝 시간으로 변환 (검색 종료용)
     * 한국 시간 23:59:59.999999999을 UTC로 변환하여 해당 날짜의 마지막 시점을 반환
     * 
     * @param koreaLocalDate 변환할 한국 날짜 (null 가능)
     * @return UTC 기준 해당 날짜의 끝 시간 (입력이 null이면 null 반환)
     *         예: 2024-12-31 -> 2025-01-01 14:59:59.999999999+00
     */
    public static OffsetDateTime toUtcEndOfDay(final LocalDate koreaLocalDate) {
        if (koreaLocalDate == null) {
            return null;
        }
        return koreaLocalDate.atTime(23, 59, 59, 999999999)
                .atOffset(AppConstants.KOREA_ZONE_OFFSET)
                .toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * 날짜 범위 검색을 위한 UTC 시작/종료 시간 쌍 반환
     * 
     * @param koreaLocalDate 검색할 한국 날짜 (null 가능)
     * @return [시작시간, 종료시간] 배열 (입력이 null이면 [null, null] 반환)
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
