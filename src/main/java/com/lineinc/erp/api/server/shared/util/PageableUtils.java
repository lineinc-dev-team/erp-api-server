package com.lineinc.erp.api.server.shared.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

public class PageableUtils {

    /**
     * 페이지 번호, 페이지 크기를 기반으로 Pageable 생성
     */
    public static Pageable createPageable(final int page, final int size) {
        return PageRequest.of(page, size);
    }

    /**
     * 페이지 번호, 페이지 크기, 정렬 조건 문자열을 기반으로 Pageable 생성
     */
    public static Pageable createPageable(final int page, final int size, final String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, parseSort(sort));
    }

    /**
     * 정렬 조건 문자열을 Spring Sort 객체로 변환
     * 예: "username,desc;createdAt,asc"
     */
    public static Sort parseSort(final String sortString) {
        if (sortString == null || sortString.isBlank()) {
            return Sort.unsorted();
        }

        final String[] sortParams = sortString.split(";");
        final List<Sort.Order> orders = new ArrayList<>();

        for (final String param : sortParams) {
            final String[] parts = param.trim().split(",");
            final String field = parts[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) {
                direction = Sort.Direction.DESC;
            }

            orders.add(new Sort.Order(direction, field));
        }
        return Sort.by(orders);
    }

    public static OrderSpecifier<?>[] toOrderSpecifiers(
            final Pageable pageable,
            final Map<String, ComparableExpressionBase<?>> fieldMapping) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[0]; // 기본 정렬 없이 빈 배열 반환
        }
        return toOrderSpecifiers(pageable.getSort(), fieldMapping);
    }

    public static OrderSpecifier<?>[] toOrderSpecifiers(
            final Sort sort,
            final Map<String, ComparableExpressionBase<?>> fieldMapping) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier[0];
        }

        return sort.stream()
                .map(order -> {
                    final Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    final ComparableExpressionBase<?> path = fieldMapping.get(order.getProperty());

                    if (path == null) {
                        return null; // 매핑 안 된 필드는 무시
                    }

                    return new OrderSpecifier<>(direction, path);
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }

    /**
     * PageRequest, SortRequest로부터 Pageable을 생성하는 편의 메서드
     * 
     * @param pageRequest 페이지 요청 객체
     * @param sortRequest 정렬 요청 객체
     * @return Pageable 객체
     */
    public static Pageable createPageable(
            final com.lineinc.erp.api.server.shared.dto.request.PageRequest pageRequest,
            final SortRequest sortRequest) {
        return createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort());
    }

    /**
     * List, Pageable, total을 사용해 Page 객체를 생성하는 편의 메서드
     * 
     * @param content  페이지 내용
     * @param pageable 페이지 정보
     * @param total    전체 요소 수
     * @return Page 객체
     */
    public static <T> Page<T> createPage(final List<T> content, final Pageable pageable, final long total) {
        return new PageImpl<>(content, pageable, total);
    }
}