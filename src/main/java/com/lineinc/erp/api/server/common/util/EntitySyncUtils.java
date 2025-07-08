package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.domain.common.entity.interfaces.MarkDeletable;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

@Slf4j
public class EntitySyncUtils {

    /**
     * 엔티티 리스트와 요청 리스트를 ID 기준으로 동기화합니다.
     *
     * <p>이 메서드는 아래의 세 가지 동작을 수행합니다:
     * <ul>
     *     <li><b>Soft delete</b>: 기존 엔티티 중 요청 리스트에 존재하지 않는 항목을 markAsDeleted()</li>
     *     <li><b>Update</b>: 요청 리스트의 ID가 기존 엔티티에 존재하면 updateFrom() 호출</li>
     *     <li><b>Create</b>: 요청 리스트에만 존재하는 항목은 creator.apply()로 생성 후 리스트에 추가</li>
     * </ul>
     *
     * <p>리플렉션을 사용하여 엔티티와 요청 DTO에서 getId() 메서드를 자동으로 호출합니다.
     *
     * @param existingEntities 현재 보유 중인 엔티티 리스트 (수정 가능한 컬렉션이어야 함)
     * @param requests         동기화할 요청 리스트 (DTO 등)
     * @param creator          ID가 없는 요청으로부터 새 엔티티를 생성하는 함수
     * @param <T>              엔티티 타입 (MarkDeletable 및 UpdatableFrom<R> 구현 필요)
     * @param <R>              요청 DTO 타입
     */
    public static <T extends MarkDeletable & UpdatableFrom<R>, R> void syncList(
            List<T> existingEntities,
            List<R> requests,
            Function<R, T> creator
    ) {
        if (requests == null) {
            // 요청이 null이면 아무 작업도 수행하지 않음
            return;
        }

        if (requests.isEmpty()) {
            // 요청이 빈 리스트라면 기존 엔티티 모두 삭제
            existingEntities.forEach(MarkDeletable::markAsDeleted);
            return;
        }

        // 1. 기존 엔티티에서 ID가 있는 항목만 Map으로 변환 (ID → Entity)
        Map<Long, T> existingMap = existingEntities.stream()
                .filter(e -> extractId(e) != null)
                .collect(Collectors.toMap(EntitySyncUtils::extractId, Function.identity()));

        // 2. 요청 리스트에서 null이 아닌 ID만 추출하여 Set 생성
        Set<Long> requestIds = requests.stream()
                .map(EntitySyncUtils::extractId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 요청에 없는 기존 엔티티는 삭제 대상으로 간주 → soft delete 처리
        existingEntities.stream()
                .filter(e -> {
                    Long id = extractId(e);
                    return id != null && !requestIds.contains(id);
                })
                .forEach(MarkDeletable::markAsDeleted);

        // 4. 요청 처리: 업데이트 또는 생성
        List<T> newEntities = new ArrayList<>();

        for (R request : requests) {
            Long id = extractId(request);

            if (id != null && existingMap.containsKey(id)) {
                // 기존 엔티티 업데이트
                T existingEntity = existingMap.get(id);
                existingEntity.updateFrom(request);
            } else {
                // 새 엔티티 생성 (ID가 없거나 기존에 존재하지 않는 경우)
                T newEntity = creator.apply(request);
                newEntities.add(newEntity);
            }
        }

        // 5. 새 엔티티들을 기존 리스트에 추가
        existingEntities.addAll(newEntities);
    }

    /**
     * 리플렉션을 사용하여 객체에서 getId() 메서드를 호출하고 Long 타입의 ID를 반환합니다.
     */
    private static Long extractId(Object obj) {
        try {
            try {
                return (Long) obj.getClass().getMethod("getId").invoke(obj);
            } catch (NoSuchMethodException ignored) {
                return (Long) obj.getClass().getMethod("id").invoke(obj);
            }
        } catch (Exception e) {
            log.warn("⚠️ extractId 실패 - 클래스: {}", obj.getClass().getName(), e);
            return null;
        }
    }
}