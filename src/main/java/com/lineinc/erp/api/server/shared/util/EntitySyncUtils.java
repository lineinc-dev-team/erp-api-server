package com.lineinc.erp.api.server.shared.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntitySyncUtils {

    /**
     * 엔티티 리스트와 요청 리스트를 ID 기준으로 동기화합니다.
     *
     * <p>
     * 이 메서드는 아래의 세 가지 동작을 수행합니다:
     * <ul>
     * <li><b>Soft delete</b>: 기존 엔티티 중 요청 리스트에 존재하지 않는 항목을 markAsDeleted()</li>
     * <li><b>Update</b>: 요청 리스트의 ID가 기존 엔티티에 존재하면 updateFrom() 호출</li>
     * <li><b>Create</b>: 요청 리스트에만 존재하는 항목은 creator.apply()로 생성 후 리스트에 추가</li>
     * </ul>
     *
     * <p>
     * 리플렉션을 사용하여 엔티티와 요청 DTO에서 getId() 메서드를 자동으로 호출합니다.
     *
     * @param existingEntities 현재 보유 중인 엔티티 리스트 (수정 가능한 컬렉션이어야 함)
     * @param requests         동기화할 요청 리스트 (DTO 등)
     * @param creator          ID가 없는 요청으로부터 새 엔티티를 생성하는 함수
     * @param <T>              엔티티 타입
     * @param <R>              요청 DTO 타입
     */
    public static <T, R> void syncList(
            List<T> existingEntities,
            List<R> requests,
            Function<R, T> creator) {
        if (requests == null) {
            log.debug("요청 리스트가 null이므로 동기화를 건너뜁니다.");
            return;
        }

        if (requests.isEmpty()) {
            log.debug("요청 리스트가 비어있으므로 기존 엔티티를 모두 삭제 처리합니다.");
            markEntitiesAsDeleted(existingEntities);
            return;
        }

        // 1. 기존 엔티티에서 ID가 있는 항목만 Map으로 변환 (ID → Entity)
        Map<Long, T> existingMap = createExistingEntitiesMap(existingEntities);

        // 2. 요청 리스트에서 null이 아닌 ID만 추출하여 Set 생성
        Set<Long> requestIds = extractRequestIds(requests);

        // 3. 요청에 없는 기존 엔티티는 삭제 대상으로 간주 → soft delete 처리
        markUnusedEntitiesAsDeleted(existingEntities, requestIds);

        // 4. 요청 처리: 업데이트 또는 생성
        List<T> newEntities = processRequests(requests, existingMap, creator);

        // 5. 새 엔티티들을 기존 리스트에 추가
        existingEntities.addAll(newEntities);
    }

    /**
     * 기존 엔티티들을 Map으로 변환합니다.
     */
    private static <T> Map<Long, T> createExistingEntitiesMap(List<T> existingEntities) {
        return existingEntities.stream()
                .filter(e -> extractId(e) != null)
                .collect(Collectors.toMap(
                        EntitySyncUtils::extractId,
                        Function.identity(),
                        (existing, _) -> {
                            log.warn("중복된 ID 발견: {}", extractId(existing));
                            return existing; // 기존 엔티티 유지
                        }));
    }

    /**
     * 요청 리스트에서 ID를 추출합니다.
     */
    private static <R> Set<Long> extractRequestIds(List<R> requests) {
        return requests.stream()
                .map(EntitySyncUtils::extractId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 사용되지 않는 엔티티들을 삭제 처리합니다.
     */
    private static <T> void markUnusedEntitiesAsDeleted(List<T> existingEntities, Set<Long> requestIds) {
        existingEntities.stream()
                .filter(e -> {
                    Long id = extractId(e);
                    return id != null && !requestIds.contains(id);
                })
                .forEach(EntitySyncUtils::markEntityAsDeleted);
    }

    /**
     * 요청을 처리하여 새 엔티티를 생성하거나 기존 엔티티를 업데이트합니다.
     */
    private static <T, R> List<T> processRequests(
            List<R> requests,
            Map<Long, T> existingMap,
            Function<R, T> creator) {
        List<T> newEntities = new ArrayList<>();

        for (R request : requests) {
            Long id = extractId(request);

            if (id != null && existingMap.containsKey(id)) {
                // 기존 엔티티 업데이트
                updateExistingEntity(existingMap.get(id), request);
            } else {
                // 새 엔티티 생성 (ID가 없거나 기존에 존재하지 않는 경우)
                T newEntity = creator.apply(request);
                newEntities.add(newEntity);
            }
        }

        return newEntities;
    }

    /**
     * 기존 엔티티를 업데이트합니다.
     */
    private static <T, R> void updateExistingEntity(T existingEntity, R request) {
        try {
            // updateFrom 메서드가 있는 경우에만 호출
            existingEntity.getClass()
                    .getMethod("updateFrom", request.getClass())
                    .invoke(existingEntity, request);
        } catch (Exception e) {
            log.warn("엔티티 업데이트 실패: {} -> {}",
                    existingEntity.getClass().getSimpleName(),
                    request.getClass().getSimpleName(), e);
        }
    }

    /**
     * 엔티티를 삭제 처리합니다.
     */
    private static <T> void markEntityAsDeleted(T entity) {
        try {
            // markAsDeleted 메서드가 있는 경우에만 호출
            entity.getClass().getMethod("markAsDeleted").invoke(entity);
        } catch (Exception e) {
            log.warn("엔티티 삭제 처리 실패: {}", entity.getClass().getSimpleName(), e);
        }
    }

    /**
     * 엔티티 리스트를 모두 삭제 처리합니다.
     */
    private static <T> void markEntitiesAsDeleted(List<T> entities) {
        entities.forEach(EntitySyncUtils::markEntityAsDeleted);
    }

    /**
     * 리플렉션을 사용하여 객체에서 ID를 추출합니다.
     * 
     * @param obj ID를 추출할 객체
     * @return ID 값 (Long), 추출 실패 시 null
     */
    private static Long extractId(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            // getId() 메서드 시도
            return (Long) obj.getClass().getMethod("getId").invoke(obj);
        } catch (NoSuchMethodException ignored) {
            try {
                // id() 메서드 시도 (record 클래스용)
                return (Long) obj.getClass().getMethod("id").invoke(obj);
            } catch (Exception ignored2) {
                // 두 메서드 모두 실패
                return null;
            }
        } catch (Exception e) {
            log.debug("ID 추출 실패 - 클래스: {}", obj.getClass().getSimpleName());
            return null;
        }
    }
}