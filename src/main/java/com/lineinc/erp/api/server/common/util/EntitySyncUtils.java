package com.lineinc.erp.api.server.common.util;

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
     * @param requestIdExtractor 요청에서 ID를 추출하는 함수
     * @param entityIdExtractor  엔티티에서 ID를 추출하는 함수
     * @param updater            기존 엔티티를 요청 기반으로 업데이트하는 함수
     * @param creator            요청으로부터 새 엔티티를 생성하는 함수
     * @param softDeleter        요청에 없는 기존 엔티티를 soft delete 하는 함수
     * @param adder              새 엔티티를 기존 리스트에 추가하는 함수
     * @param <T>                엔티티 타입
     * @param <R>                요청 DTO 타입
     */
    public static <T, R> void syncList(
            List<T> existingEntities,
            List<R> requests,
            Function<R, Long> requestIdExtractor,
            Function<T, Long> entityIdExtractor,
            BiConsumer<T, R> updater,
            Function<R, T> creator,
            Consumer<T> softDeleter,
            Consumer<T> adder
    ) {
        if (requests == null) return;

        Map<Long, T> existingMap = mapById(existingEntities, entityIdExtractor);
        Set<Long> requestIds = extractRequestIds(requests, requestIdExtractor);

        removeMissingEntities(existingEntities, requestIds, entityIdExtractor, softDeleter);
        updateExistingEntities(existingMap, requests, requestIdExtractor, updater);
        createNewEntities(existingMap, requests, requestIdExtractor, creator, adder);
    }

    /**
     * 리스트를 ID 기준으로 Map 으로 변환합니다.
     */
    private static <T> Map<Long, T> mapById(List<T> list, Function<T, Long> idExtractor) {
        return list.stream()
                .filter(e -> idExtractor.apply(e) != null)
                .collect(Collectors.toMap(idExtractor, Function.identity()));
    }

    /**
     * 요청 리스트에서 ID 집합을 추출합니다.
     */
    private static <R> Set<Long> extractRequestIds(List<R> requests, Function<R, Long> idExtractor) {
        return requests.stream()
                .map(idExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 기존 엔티티 중 요청 ID에 없는 항목을 soft delete 처리합니다.
     */
    private static <T> void removeMissingEntities(
            List<T> existingEntities,
            Set<Long> requestIds,
            Function<T, Long> idExtractor,
            Consumer<T> softDeleter
    ) {
        existingEntities.stream()
                .filter(e -> {
                    Long id = idExtractor.apply(e);
                    return id != null && !requestIds.contains(id);
                })
                .forEach(softDeleter);
    }

    /**
     * 요청에 해당하는 기존 엔티티가 있으면 업데이트 수행합니다.
     */
    private static <T, R> void updateExistingEntities(
            Map<Long, T> existingMap,
            List<R> requests,
            Function<R, Long> idExtractor,
            BiConsumer<T, R> updater
    ) {
        for (R request : requests) {
            Long id = idExtractor.apply(request);
            if (id != null && existingMap.containsKey(id)) {
                updater.accept(existingMap.get(id), request);
            }
        }
    }

    /**
     * 요청 중 ID가 없거나 기존 엔티티에 없는 경우 새 엔티티를 생성하여 추가합니다.
     */
    private static <T, R> void createNewEntities(
            Map<Long, T> existingMap,
            List<R> requests,
            Function<R, Long> idExtractor,
            Function<R, T> creator,
            Consumer<T> adder
    ) {
        for (R request : requests) {
            Long id = idExtractor.apply(request);
            if (id == null || !existingMap.containsKey(id)) {
                adder.accept(creator.apply(request));
            }
        }
    }
}