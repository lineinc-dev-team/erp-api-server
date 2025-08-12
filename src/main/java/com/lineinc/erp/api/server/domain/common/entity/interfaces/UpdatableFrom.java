package com.lineinc.erp.api.server.domain.common.entity.interfaces;

/**
 * 엔티티가 특정 요청 객체로부터 업데이트될 수 있음을 나타내는 인터페이스
 * @param <T> 업데이트 요청 객체의 타입
 */
public interface UpdatableFrom<T> {
    
    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     * @param request 업데이트 요청 객체
     */
    void updateFrom(T request);
}
