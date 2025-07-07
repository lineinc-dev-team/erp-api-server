package com.lineinc.erp.api.server.domain.common.entity.interfaces;

/**
 * soft delete를 위한 markAsDeleted 메서드를 구현해야 하는 인터페이스입니다.
 */
public interface MarkDeletable {
    void markAsDeleted();
}