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

public class PageableUtils {

    /**
     * 페이지 번호, 페이지 크기, 정렬 조건 문자열을 기반으로 Pageable 생성
     */
    public static Pageable createPageable(int page, int size, String sort) {
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

    /**
     * Pageable의 Sort를 QueryDSL OrderSpecifier 배열로 변환
     *
     * @param pageable     페이징 정보
     * @param fieldMapping 필드명과 QueryDSL Path의 매핑
     * @param defaultOrder 정렬 조건이 없을 때 사용할 기본 정렬
     * @return OrderSpecifier 배열
     */
    public static OrderSpecifier<?>[] toOrderSpecifiers(
            Pageable pageable,
            Map<String, ComparableExpressionBase<?>> fieldMapping,
            OrderSpecifier<?> defaultOrder) {

        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{defaultOrder};
        }

        return pageable.getSort().stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    ComparableExpressionBase<?> path = fieldMapping.get(order.getProperty());

                    // 매핑되지 않은 필드는 기본 정렬 사용
                    if (path == null) {
                        return defaultOrder;
                    }

                    return new OrderSpecifier<>(direction, path);
                })
                .toArray(OrderSpecifier[]::new);
    }

}