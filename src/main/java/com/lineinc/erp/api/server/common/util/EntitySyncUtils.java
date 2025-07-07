package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.domain.common.entity.interfaces.MarkDeletable;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class EntitySyncUtils {

    /**
     * 엔티티 리스트와 요청 리스트를 ID 기준으로 동기화합니다.
     *
     * <p>이 메서드는 아래의 세 가지 동작을 수행합니다:
     * <ul>
     *     <li><b>Soft delete</b>: 기존 엔티티 중 요청 리스트에 존재하지 않는 항목을 markAsDeleted()</li>
     *     <li><b>Update</b>: 요청 리스트의 ID가 기존 엔티티에 존재하면 updateFrom() 호출</li>
     *     <li><b>Create</b>: 요청 리스트에만 존재하는 항목은 creator.apply()로 생성 후 adder.accept()로 추가</li>
     * </ul>
     *
     * @param existingEntities   현재 보유 중인 엔티티 리스트 (수정 가능한 컬렉션이어야 함)
     * @param requests           동기화할 요청 리스트 (DTO 등)
     * @param entityIdExtractor  엔티티에서 ID를 추출하는 함수 (e.g., Entity::getId)
     * @param requestIdExtractor 요청 DTO에서 ID를 추출하는 함수 (e.g., dto -> dto.id())
     * @param creator            ID가 없는 요청으로부터 새 엔티티를 생성하는 함수
     * @param adder              새로 생성된 엔티티를 기존 리스트에 추가하는 함수 (보통 리스트의 add 메서드 참조)
     * @param <T>                엔티티 타입 (MarkDeletable 및 UpdatableFrom<R> 구현 필요)
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

        // 1. 기존 엔티티에서 ID가 있는 항목만 Map으로 변환 (ID → Entity)
        Map<Long, T> existingMap = existingEntities.stream()
                .filter(e -> entityIdExtractor.apply(e) != null)
                .collect(Collectors.toMap(entityIdExtractor, Function.identity()));

        // 2. 요청 리스트에서 ID만 추출하여 Set 생성
        Set<Long> requestIds = requests.stream()
                .map(requestIdExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 요청에 없는 기존 엔티티는 삭제 대상으로 간주 → soft delete 처리
        existingEntities.stream()
                .filter(e -> {
                    Long id = entityIdExtractor.apply(e);
                    return id != null && !requestIds.contains(id);
                })
                .forEach(MarkDeletable::markAsDeleted);

        // 4. 요청에 ID가 있고, 기존 엔티티에 존재하면 updateFrom()으로 업데이트
        for (R request : requests) {
            Long id = requestIdExtractor.apply(request);
            if (id != null && existingMap.containsKey(id)) {
                T existingEntity = existingMap.get(id);
                existingEntity.updateFrom(request);
            }
        }

        // 5. 요청에 ID가 없거나 기존에 존재하지 않으면 새 엔티티 생성 및 추가
        for (R request : requests) {
            Long id = requestIdExtractor.apply(request);
            if (id == null || !existingMap.containsKey(id)) {
                T newEntity = creator.apply(request);
                adder.accept(newEntity);
            }
        }
    }


}
