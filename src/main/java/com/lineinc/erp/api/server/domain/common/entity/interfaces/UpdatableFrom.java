package com.lineinc.erp.api.server.domain.common.entity.interfaces;

/**
 * 요청 DTO로부터 엔티티를 업데이트하는 메서드를 구현해야 하는 인터페이스입니다.
 *
 * @param <R> 요청 DTO 타입
 */
public interface UpdatableFrom<R> {
    void updateFrom(R request);
}