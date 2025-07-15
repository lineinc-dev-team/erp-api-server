package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;

import java.util.List;

public class DownloadFieldUtils {

    public static List<String> parseFields(String fields) {
        return fields == null || fields.isBlank()
                ? List.of()
                : List.of(fields.split("\\s*,\\s*"));
    }

    public static void validateFields(List<String> fields, List<String> allowedFields) {
        for (String field : fields) {
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException(ValidationMessages.INVALID_DOWNLOAD_FIELD + field);
            }
        }
    }
}