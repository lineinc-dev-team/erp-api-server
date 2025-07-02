package com.lineinc.erp.api.server.common.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

public class QueryDslUtils {

    private QueryDslUtils() {
    }

    public static BooleanExpression containsIgnoreCase(StringPath path, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return path.containsIgnoreCase(value);
    }

}