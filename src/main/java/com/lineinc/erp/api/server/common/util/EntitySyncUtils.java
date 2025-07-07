package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.domain.common.entity.interfaces.MarkDeletable;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * ID 기반으로 기존 엔티티 리스트와 요청 리스트를 동기화하는 유틸리티 클래스입니다.
 * <p>
 * 주요 동작:
 * <ul>
 *     <li>기존 엔티티가 요청에 없으면 soft delete 처리</li>
 *     <li>요청과 기존 엔티티 ID가 일치하면 업데이트 수행</li>
 *     <li>요청에만 존재하는 경우 신규 엔티티 생성 및 추가</li>
 * </ul>
 */
public class EntitySyncUtils {

    /**
     * 엔티티 리스트와 요청 리스트를 ID 기준으로 동기화합니다.
     *
     * @param existingEntities   기존 엔티티 리스트
     * @param requests           요청 리스트 (DTO 등)
     * @param entityIdExtractor  엔티티에서 ID를 추출하는 함수
     * @param requestIdExtractor 요청에서 ID를 추출하는 함수
     * @param creator            요청으로부터 새 엔티티를 생성하는 함수
     * @param adder              새 엔티티를 기존 리스트에 추가하는 함수
     * @param <T>                엔티티 타입, MarkDeletable.java, UpdatableFrom<R>를 구현해야 함
     * @param <R>                요청 DTO 타입
     */
    public static <T extends MarkDeletable & UpdatableFrom<R>, R> void syncList(
            List<T> existingEntities,
            List<R> requests,
            Function<T, Long> entityIdExtractor,
            Function<R, Long> requestIdExtractor,
            Function<R, T> creator,
            Consumer<T> adder
    ) {
        if (requests == null) return;

        Map<Long, T> existingMap = existingEntities.stream()
                .filter(e -> entityIdExtractor.apply(e) != null)
                .collect(Collectors.toMap(entityIdExtractor, Function.identity()));

        Set<Long> requestIds = requests.stream()
                .map(requestIdExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEntities.stream()
                .filter(e -> {
                    Long id = entityIdExtractor.apply(e);
                    return id != null && !requestIds.contains(id);
                })
                .forEach(MarkDeletable::markAsDeleted);

        for (R request : requests) {
            Long id = requestIdExtractor.apply(request);
            if (id != null && existingMap.containsKey(id)) {
                T existingEntity = existingMap.get(id);
                existingEntity.updateFrom(request);
            }
        }

        for (R request : requests) {
            Long id = requestIdExtractor.apply(request);
            if (id == null || !existingMap.containsKey(id)) {
                T newEntity = creator.apply(request);
                adder.accept(newEntity);
            }
        }
    }


}
