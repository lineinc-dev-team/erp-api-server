package com.lineinc.erp.api.server.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

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
     * Pageable로부터 정렬 정보만 추출하여 QueryDSL에서 사용할 수 있는 형태로 가공
     * (필요시 OrderSpecifier로 변환 가능)
     */
    public static List<Sort.Order> extractOrders(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return List.of();
        }
        return pageable.getSort().toList();
    }
}