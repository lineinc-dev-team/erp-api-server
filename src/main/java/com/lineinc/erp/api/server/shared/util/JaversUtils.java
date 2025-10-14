package com.lineinc.erp.api.server.shared.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.labor.entity.LaborFile;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriverFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaversUtils {

    private static final Map<Class<?>, Function<Object, String>> ENTITY_NAME_EXTRACTORS = Map.ofEntries(
            Map.entry(ClientCompanyContact.class, entity -> ((ClientCompanyContact) entity).getName()),
            Map.entry(ClientCompanyFile.class, entity -> ((ClientCompanyFile) entity).getName()),
            Map.entry(MaterialManagementDetail.class, entity -> ((MaterialManagementDetail) entity).getName()),
            Map.entry(MaterialManagementFile.class, entity -> ((MaterialManagementFile) entity).getName()),
            Map.entry(SiteContract.class, entity -> ((SiteContract) entity).getName()),
            Map.entry(SiteFile.class, entity -> ((SiteFile) entity).getOriginalFileName()),
            Map.entry(OutsourcingCompanyContact.class, entity -> ((OutsourcingCompanyContact) entity).getName()),
            Map.entry(OutsourcingCompanyFile.class, entity -> ((OutsourcingCompanyFile) entity).getName()),
            Map.entry(OutsourcingCompanyContractContact.class,
                    entity -> ((OutsourcingCompanyContractContact) entity).getName()),
            Map.entry(OutsourcingCompanyContractFile.class,
                    entity -> ((OutsourcingCompanyContractFile) entity).getName()),
            Map.entry(OutsourcingCompanyContractWorker.class,
                    entity -> ((OutsourcingCompanyContractWorker) entity).getName()),
            Map.entry(OutsourcingCompanyContractWorkerFile.class,
                    entity -> ((OutsourcingCompanyContractWorkerFile) entity).getOriginalFileName()),
            Map.entry(OutsourcingCompanyContractEquipment.class,
                    entity -> ((OutsourcingCompanyContractEquipment) entity).getSpecification()),
            Map.entry(OutsourcingCompanyContractSubEquipment.class,
                    entity -> ((OutsourcingCompanyContractSubEquipment) entity).getType().getLabel()),
            Map.entry(OutsourcingCompanyContractDriver.class,
                    entity -> ((OutsourcingCompanyContractDriver) entity).getName()),
            Map.entry(OutsourcingCompanyContractDriverFile.class,
                    entity -> ((OutsourcingCompanyContractDriverFile) entity).getOriginalFileName()),
            Map.entry(OutsourcingCompanyContractConstruction.class,
                    entity -> ((OutsourcingCompanyContractConstruction) entity).getItem()),
            Map.entry(FuelInfo.class, entity -> ((FuelInfo) entity).getOutsourcingCompanyName()),
            Map.entry(LaborFile.class, entity -> ((LaborFile) entity).getName()),
            Map.entry(ManagementCostDetail.class, entity -> ((ManagementCostDetail) entity).getName()),
            Map.entry(ManagementCostKeyMoneyDetail.class,
                    entity -> ((ManagementCostKeyMoneyDetail) entity).getAccount()),
            Map.entry(ManagementCostMealFeeDetail.class, entity -> {
                final ManagementCostMealFeeDetail detail = (ManagementCostMealFeeDetail) entity;
                final String name = detail.getName();
                if (name != null && !name.trim().isEmpty()) {
                    return name;
                }
                // name이 비어있으면 labor의 name 사용
                if (detail.getLabor() != null && detail.getLabor().getName() != null
                        && !detail.getLabor().getName().trim().isEmpty()) {
                    return detail.getLabor().getName();
                }
                return null;
            }),
            Map.entry(ManagementCostFile.class, entity -> ((ManagementCostFile) entity).getName()),
            Map.entry(SteelManagementDetailV2.class, entity -> {
                final SteelManagementDetailV2 detail = (SteelManagementDetailV2) entity;
                // specification이 없으면 name 사용
                if (detail.getSpecification() != null && !detail.getSpecification().trim().isEmpty()) {
                    return detail.getSpecification();
                }
                return detail.getName();
            }));

    // ================== Snapshot ==================
    public static <T> T createSnapshot(final Javers javers, final T entity, final Class<T> clazz) {
        try {
            final Object unproxied = unproxyRecursive(entity);
            final String json = javers.getJsonConverter().toJson(unproxied);
            return javers.getJsonConverter().fromJson(json, clazz);
        } catch (final Exception e) {
            log.error("❌ createSnapshot 직렬화 실패 클래스: {}", entity.getClass().getName(), e);
            throw e;
        }
    }

    // ================== 변경 내역 ==================
    public static List<Map<String, String>> extractModifiedChanges(final Javers javers, final Diff diff) {
        return diff.getChanges().stream()
                .filter(c -> c instanceof ValueChange)
                .map(c -> (ValueChange) c)
                .map(vc -> {
                    // 삭제 변경 처리 (deleted: false -> true)
                    if ("deleted".equals(vc.getPropertyName())
                            && Boolean.FALSE.equals(vc.getLeft())
                            && Boolean.TRUE.equals(vc.getRight())) {
                        final String beforeValue = formatDeletedContactName(vc.getAffectedObject());

                        // 의미있는 식별자가 없으면 히스토리를 생성하지 않음
                        if (beforeValue == null || beforeValue.trim().isEmpty() || "항목".equals(beforeValue)) {
                            return null;
                        }

                        return createChangeMap("null", beforeValue, "null", "삭제", vc.getAffectedObject());
                    }

                    // 시스템 전용 변경은 제외
                    if (isSystemOnlyChange(vc)) {
                        return null;
                    }

                    // before와 after 값이 실제로 다른지 확인
                    final String beforeValue = toJsonSafe(javers, vc.getLeft());
                    final String afterValue = toJsonSafe(javers, vc.getRight());

                    // 값이 동일하면 변경 이력에 포함하지 않음
                    if (beforeValue.equals(afterValue)) {
                        return null;
                    }

                    // 수정 변경
                    return createChangeMap(vc.getPropertyName(), beforeValue, afterValue, "수정", vc.getAffectedObject());
                })
                .filter(change -> change != null) // null 제외
                .collect(Collectors.toList());
    }

    // 삭제 전용 변경 감지
    public static boolean isDeletedChange(final Diff diff) {
        return diff.getChanges().stream()
                .filter(c -> c instanceof ValueChange)
                .map(c -> (ValueChange) c)
                .anyMatch(vc -> "deleted".equals(vc.getPropertyName()) &&
                        Boolean.TRUE.equals(vc.getRight()));
    }

    // 시스템 전용 변경인지 확인 (일반 수정 이력에서 제외)
    private static boolean isSystemOnlyChange(final ValueChange vc) {
        final String propertyName = vc.getPropertyName();

        // deleted 필드는 삭제 시에만 추적하고, 일반 수정에서는 제외
        if ("deleted".equals(propertyName)) {
            return true;
        }

        // deletedAt도 시스템 필드이므로 제외
        if ("deletedAt".equals(propertyName)) {
            return true;
        }

        return false;
    }

    public static Map<String, String> extractAddedEntityChange(final Javers javers, final Object newEntity) {
        final String afterValue = extractEntityName(newEntity);
        if (afterValue == null || afterValue.trim().isEmpty()) {
            // 의미있는 식별자가 없으면 히스토리를 생성하지 않음
            return null;
        }

        return createChangeMap("null", "null", afterValue, "추가", newEntity);
    }

    /**
     * 변경 이력 Map 생성 (중복 제거)
     */
    private static Map<String, String> createChangeMap(final String property, final String before,
            final String after, final String type, final Object affectedObject) {
        final String entityId = extractEntityId(affectedObject);
        return Map.of(
                "property", property,
                "before", before,
                "after", after,
                "type", type,
                "entityId", entityId != null ? entityId : "null");
    }

    // ================== 공통 유틸 ==================
    private static String extractEntityName(final Object entity) {
        if (entity == null)
            return null;
        final Function<Object, String> extractor = ENTITY_NAME_EXTRACTORS.get(entity.getClass());
        if (extractor != null) {
            try {
                return extractor.apply(entity);
            } catch (final Exception e) {
                log.warn("엔티티 이름 추출 실패: {}", entity.getClass().getSimpleName(), e);
            }
        }
        return null;
    }

    /**
     * 엔티티에서 ID 추출
     */
    private static String extractEntityId(Object entity) {
        if (entity == null) {
            return null;
        }

        // Optional unwrap
        if (entity instanceof Optional<?>) {
            entity = ((Optional<?>) entity).orElse(null);
            if (entity == null) {
                return null;
            }
        }

        try {
            final java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
            final Object id = getIdMethod.invoke(entity);
            return id != null ? id.toString() : null;
        } catch (final Exception e) {
            log.debug("ID 추출 실패: {}", entity.getClass().getSimpleName());
            return null;
        }
    }

    private static String toJsonSafe(final Javers javers, final Object obj) {
        if (obj == null)
            return "";
        if (obj instanceof String || obj instanceof Boolean || obj instanceof Character) {
            return obj.toString();
        }
        if (obj instanceof Number) {
            if (obj instanceof BigDecimal) {
                return ((BigDecimal) obj).stripTrailingZeros().toPlainString();
            }
            // 4자리 이상 정수에만 천 단위 구분자 추가
            final double value = ((Number) obj).doubleValue();
            if (value >= 1000 && value == Math.floor(value)) {
                return NumberFormat.getNumberInstance().format((long) value);
            }
            return obj.toString();
        }
        return javers.getJsonConverter().toJson(unproxyRecursive(obj));
    }

    private static String formatDeletedContactName(Object affectedObject) {
        if (affectedObject instanceof final Optional<?> optional) {
            affectedObject = optional.orElse(null);
        }

        if (affectedObject == null) {
            return "항목";
        }

        final String entityName = extractEntityName(affectedObject);
        if (entityName != null) {
            return entityName;
        }

        return "항목";
    }

    // ================== Hibernate Proxy 제거 + 순환참조 방지 ==================
    private static Object unproxyRecursive(final Object entity) {
        final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return unproxyRecursive(entity, visited);
    }

    private static Object unproxyRecursive(Object entity, final Set<Object> visited) {
        if (entity == null || visited.contains(entity))
            return entity;
        visited.add(entity);

        if (entity instanceof final HibernateProxy proxy) {
            entity = Hibernate.unproxy(proxy);
        }

        // 이미 초기화된 필드만 처리 (Lazy Loading 방지)
        for (final Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                // Hibernate 프록시나 PersistentCollection인 경우 초기화 여부 확인
                if (isHibernateLazyField(entity, field)) {
                    continue; // 초기화되지 않은 Lazy 필드는 건드리지 않음
                }

                final Object value = field.get(entity);
                if (value == null || visited.contains(value))
                    continue;

                if (value instanceof final Iterable<?> iterable) {
                    for (final Object item : iterable) {
                        unproxyRecursive(item, visited);
                    }
                } else if (value instanceof final Map<?, ?> map) {
                    for (final Map.Entry<?, ?> entry : map.entrySet()) {
                        unproxyRecursive(entry.getKey(), visited);
                        unproxyRecursive(entry.getValue(), visited);
                    }
                } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                    final Object unproxiedValue = unproxyRecursive(value, visited);
                    field.set(entity, unproxiedValue);
                }
            } catch (final Exception ignored) {
                // 접근 실패한 필드는 무시 (Lazy Loading 에러 등)
            }
        }

        return entity;
    }

    // Hibernate Lazy 필드 초기화 여부 확인
    private static boolean isHibernateLazyField(final Object entity, final Field field) {
        try {
            final Object value = field.get(entity);

            // PersistentCollection 체크 (OneToMany, ManyToMany 등)
            if (value != null && value.getClass().getName().contains("PersistentCollection")) {
                return !Hibernate.isInitialized(value);
            }

            // HibernateProxy 체크 (ManyToOne, OneToOne 등)
            if (value instanceof HibernateProxy) {
                return !Hibernate.isInitialized(value);
            }

            return false;
        } catch (final Exception e) {
            // 접근 불가능한 필드는 Lazy로 간주
            return true;
        }
    }
}