package com.lineinc.erp.api.server.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PageableUtils {

    /**
     * 페이지 번호, 페이지 크기를 기반으로 Pageable 생성
     */
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    /**
     * 페이지 번호, 페이지 크기, 정렬 조건 문자열을 기반으로 Pageable 생성
     */
    public static Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, parseSort(sort));
    }

    /**
     * 정렬 조건 문자열을 Spring Sort 객체로 변환
     * 예: "username,desc;createdAt,asc"
     */
    public static Sort parseSort(String sortString) {
        if (sortString == null || sortString.isBlank()) {
            return Sort.unsorted();
        }

        String[] sortParams = sortString.split(";");
        List<Sort.Order> orders = new ArrayList<>();

        for (String param : sortParams) {
            String[] parts = param.trim().split(",");
            String field = parts[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }

            orders.add(new Sort.Order(direction, field));
        }
        return Sort.by(orders);
    }

    public static OrderSpecifier<?>[] toOrderSpecifiers(
            Pageable pageable,
            Map<String, ComparableExpressionBase<?>> fieldMapping
    ) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[0];  // 기본 정렬 없이 빈 배열 반환
        }
        return toOrderSpecifiers(pageable.getSort(), fieldMapping);
    }

    public static OrderSpecifier<?>[] toOrderSpecifiers(
            Sort sort,
            Map<String, ComparableExpressionBase<?>> fieldMapping
    ) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier[0];
        }

        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    ComparableExpressionBase<?> path = fieldMapping.get(order.getProperty());

                    if (path == null) {
                        return null;  // 매핑 안 된 필드는 무시
                    }

                    return new OrderSpecifier<>(direction, path);
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }
}