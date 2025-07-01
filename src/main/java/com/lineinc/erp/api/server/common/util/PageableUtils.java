package com.lineinc.erp.api.server.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    /**
     * 페이지 번호, 페이지 크기, 정렬 문자열을 받아 Pageable 객체를 생성합니다.
     *
     * @param page 0부터 시작하는 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건 문자열 (여러 조건은 세미콜론(;)으로 구분, 각 조건은 "필드명,정렬방향" 형식)
     *             예: "username,desc;createdAt,asc"
     * @return Pageable 스프링 데이터 페이징 요청 객체
     */
    public static Pageable createPageable(int page, int size, String sort) {
        // 정렬 조건이 없으면 기본 PageRequest 생성
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }

        // 여러 정렬 조건을 세미콜론(;)으로 분리
        String[] sortParams = sort.split(";");

        // 초기 Sort 객체는 정렬 없음 상태로 시작
        Sort sortObj = Sort.unsorted();

        for (String param : sortParams) {
            // 각 정렬 조건은 "필드명,정렬방향" 형식으로 가정하고 쉼표(,)로 분리
            String[] parts = param.split(",");

            // 정렬할 필드명 추출, 양쪽 공백 제거
            String field = parts[0].trim();

            // 정렬 방향 기본값은 오름차순(ASC)
            Sort.Direction direction = Sort.Direction.ASC;

            // 정렬 방향이 명시되어 있고, "desc"라면 내림차순(DESC)으로 설정
            if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }

            // 현재 조건을 Sort.Order 객체로 생성
            Sort.Order order = new Sort.Order(direction, field);

            // 첫 번째 정렬 조건이면 Sort 객체를 새로 생성,
            // 그렇지 않으면 기존 Sort 객체에 조건 추가 (and 연산)
            if (sortObj.isUnsorted()) {
                sortObj = Sort.by(order);
            } else {
                sortObj = sortObj.and(Sort.by(order));
            }
        }

        // 최종적으로 정렬 조건이 반영된 PageRequest 생성 및 반환
        return PageRequest.of(page, size, sortObj);
    }
}