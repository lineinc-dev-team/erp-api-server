package com.lineinc.erp.api.server.infrastructure.config.batch.service;

/**
 * 배치 작업 서비스 인터페이스
 * 모든 배치 작업이 구현해야 하는 공통 인터페이스입니다.
 */
public interface BatchService {

    /**
     * 배치 작업을 실행합니다.
     * 
     * @return 처리된 항목 수
     * @throws Exception 배치 실행 중 발생한 예외
     */
    int execute() throws Exception;

    /**
     * 배치 작업의 이름을 반환합니다.
     * 
     * @return 배치 작업 이름
     */
    String getBatchName();
}
