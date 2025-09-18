package com.lineinc.erp.api.server.shared.util;

import java.util.List;
import java.util.Set;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;

public class DownloadFieldUtils {

    public static List<String> parseFields(final String fields) {
        return fields == null || fields.isBlank()
                ? List.of()
                : List.of(fields.split("\\s*,\\s*"));
    }

    // Set을 사용한 효율적인 검증 (O(1) 검색)
    public static void validateFields(final List<String> fields, final Set<String> allowedFields) {
        for (final String field : fields) {
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException(ValidationMessages.INVALID_DOWNLOAD_FIELD + field);
            }
        }
    }

    // 기존 List 버전 호환성 유지
    public static void validateFields(final List<String> fields, final List<String> allowedFields) {
        validateFields(fields, Set.copyOf(allowedFields));
    }
}