package com.lineinc.erp.api.server.shared.util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.*;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.managementcost.entity.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaversUtils {

    private static final Map<Class<?>, Function<Object, String>> ENTITY_NAME_EXTRACTORS = Map.ofEntries(
            Map.entry(ClientCompanyContact.class, entity -> ((ClientCompanyContact) entity).getName()),
            Map.entry(ClientCompanyFile.class, entity -> ((ClientCompanyFile) entity).getName()),
            Map.entry(MaterialManagementDetail.class, entity -> ((MaterialManagementDetail) entity).getName()),
            Map.entry(MaterialManagementFile.class, entity -> ((MaterialManagementFile) entity).getOriginalFileName()),
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
            Map.entry(SteelManagementDetail.class, entity -> ((SteelManagementDetail) entity).getName()),
            Map.entry(SteelManagementFile.class, entity -> ((SteelManagementFile) entity).getOriginalFileName()),
            Map.entry(FuelInfo.class, entity -> ((FuelInfo) entity).getEquipmentSpecification()),
            Map.entry(LaborFile.class, entity -> ((LaborFile) entity).getName()),
            Map.entry(ManagementCostDetail.class, entity -> ((ManagementCostDetail) entity).getName()),
            Map.entry(ManagementCostKeyMoneyDetail.class,
                    entity -> ((ManagementCostKeyMoneyDetail) entity).getAccount()),
            Map.entry(ManagementCostMealFeeDetail.class, entity -> ((ManagementCostMealFeeDetail) entity).getName()),
            Map.entry(ManagementCostFile.class, entity -> ((ManagementCostFile) entity).getOriginalFileName()));

    // ================== Snapshot ==================
    public static <T> T createSnapshot(Javers javers, T entity, Class<T> clazz) {
        try {
            Object unproxied = unproxyRecursive(entity);
            String json = javers.getJsonConverter().toJson(unproxied);
            return javers.getJsonConverter().fromJson(json, clazz);
        } catch (Exception e) {
            log.error("❌ createSnapshot 직렬화 실패 클래스: {}", entity.getClass().getName(), e);
            throw e;
        }
    }

    // ================== 변경 내역 ==================
    public static List<Map<String, String>> extractModifiedChanges(Javers javers, Diff diff) {
        return diff.getChanges().stream()
                .filter(c -> c instanceof ValueChange)
                .map(c -> (ValueChange) c)
                .map(vc -> {
                    // 삭제 변경 처리 (deleted: false -> true)
                    if ("deleted".equals(vc.getPropertyName())
                            && Boolean.FALSE.equals(vc.getLeft())
                            && Boolean.TRUE.equals(vc.getRight())) {
                        String beforeValue = formatDeletedContactName(vc.getAffectedObject());

                        return Map.of(
                                "property", "null",
                                "before", beforeValue,
                                "after", "null",
                                "type", "삭제");
                    }

                    // 시스템 전용 변경은 제외
                    if (isSystemOnlyChange(vc)) {
                        return null;
                    }

                    // before와 after 값이 실제로 다른지 확인
                    String beforeValue = toJsonSafe(javers, vc.getLeft());
                    String afterValue = toJsonSafe(javers, vc.getRight());

                    // 값이 동일하면 변경 이력에 포함하지 않음
                    if (beforeValue.equals(afterValue)) {
                        return null;
                    }

                    // 일반 수정 변경
                    String propertyName = vc.getPropertyName();

                    // 노무명세서의 경우 인력 이름을 포함하여 표시
                    Object affectedObject = vc.getAffectedObject();
                    if (affectedObject instanceof Optional<?> optional) {
                        affectedObject = optional.orElse(null);
                    }

                    if (isLaborPayrollEntity(affectedObject)) {
                        String laborNameWithWage = extractLaborNameWithDailyWage(affectedObject);
                        if (laborNameWithWage != null) {
                            propertyName = laborNameWithWage + " - " + propertyName;
                        }
                    }

                    return Map.of(
                            "property", propertyName,
                            "before", beforeValue,
                            "after", afterValue,
                            "type", "수정");
                })
                .filter(change -> change != null) // null 제외
                .collect(Collectors.toList());
    }

    // 삭제 전용 변경 감지
    public static boolean isDeletedChange(Diff diff) {
        return diff.getChanges().stream()
                .filter(c -> c instanceof ValueChange)
                .map(c -> (ValueChange) c)
                .anyMatch(vc -> "deleted".equals(vc.getPropertyName()) &&
                        Boolean.TRUE.equals(vc.getRight()));
    }

    // 시스템 전용 변경인지 확인 (일반 수정 이력에서 제외)
    private static boolean isSystemOnlyChange(ValueChange vc) {
        String propertyName = vc.getPropertyName();

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

    public static Map<String, String> extractAddedEntityChange(Javers javers, Object newEntity) {
        String afterValue = extractEntityName(newEntity);
        if (afterValue == null)
            afterValue = toJsonSafe(javers, newEntity);

        return Map.of(
                "property", "null",
                "before", "null",
                "after", afterValue,
                "type", "추가");
    }

    // ================== 공통 유틸 ==================
    private static String extractEntityName(Object entity) {
        if (entity == null)
            return null;
        Function<Object, String> extractor = ENTITY_NAME_EXTRACTORS.get(entity.getClass());
        if (extractor != null) {
            try {
                return extractor.apply(entity);
            } catch (Exception e) {
                log.warn("엔티티 이름 추출 실패: {}", entity.getClass().getSimpleName(), e);
            }
        }
        return null;
    }

    // 노무명세서 엔티티인지 확인
    private static boolean isLaborPayrollEntity(Object entity) {
        if (entity == null)
            return false;
        return entity.getClass().getSimpleName().equals("LaborPayroll");
    }

    // 노무명세서에서 인력 이름과 일당 추출
    private static String extractLaborNameWithDailyWage(Object entity) {
        if (entity == null)
            return null;

        try {
            // LaborPayroll 엔티티로 캐스팅하여 안전하게 접근
            if (entity instanceof LaborPayroll laborPayroll) {
                if (laborPayroll.getLabor() != null) {
                    String name = laborPayroll.getLabor().getName();
                    Integer dailyWage = laborPayroll.getDailyWage();
                    if (dailyWage != null) {
                        String formattedWage = String.format("%,d", dailyWage);
                        return name + "(" + formattedWage + "원)";
                    }
                    return name;
                }
            }
        } catch (Exception e) {
            log.warn("인력 이름 추출 실패: {}", entity.getClass().getSimpleName(), e);
        }

        return null;
    }

    private static String toJsonSafe(Javers javers, Object obj) {
        if (obj == null)
            return "";
        if (obj instanceof String || obj instanceof Boolean || obj instanceof Character)
            return obj.toString();
        if (obj instanceof Number) {
            // BigDecimal의 경우 소수점 자릿수를 정규화하여 동일한 값 비교 가능하도록 함
            if (obj instanceof java.math.BigDecimal) {
                return ((java.math.BigDecimal) obj).stripTrailingZeros().toPlainString();
            }
            return obj.toString();
        }
        return javers.getJsonConverter().toJson(unproxyRecursive(obj));
    }

    private static String formatDeletedContactName(Object affectedObject) {
        if (affectedObject instanceof Optional<?> optional) {
            affectedObject = optional.orElse(null);
        }

        if (affectedObject == null) {
            return "항목";
        }

        String entityName = extractEntityName(affectedObject);
        if (entityName != null) {
            return entityName;
        }

        return "항목";
    }

    // ================== Hibernate Proxy 제거 + 순환참조 방지 ==================
    private static Object unproxyRecursive(Object entity) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return unproxyRecursive(entity, visited);
    }

    private static Object unproxyRecursive(Object entity, Set<Object> visited) {
        if (entity == null || visited.contains(entity))
            return entity;
        visited.add(entity);

        if (entity instanceof HibernateProxy proxy) {
            entity = Hibernate.unproxy(proxy);
        }

        // 이미 초기화된 필드만 처리 (Lazy Loading 방지)
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                // Hibernate 프록시나 PersistentCollection인 경우 초기화 여부 확인
                if (isHibernateLazyField(entity, field)) {
                    continue; // 초기화되지 않은 Lazy 필드는 건드리지 않음
                }

                Object value = field.get(entity);
                if (value == null || visited.contains(value))
                    continue;

                if (value instanceof Iterable<?> iterable) {
                    for (Object item : iterable) {
                        unproxyRecursive(item, visited);
                    }
                } else if (value instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        unproxyRecursive(entry.getKey(), visited);
                        unproxyRecursive(entry.getValue(), visited);
                    }
                } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                    Object unproxiedValue = unproxyRecursive(value, visited);
                    field.set(entity, unproxiedValue);
                }
            } catch (Exception ignored) {
                // 접근 실패한 필드는 무시 (Lazy Loading 에러 등)
            }
        }

        return entity;
    }

    // Hibernate Lazy 필드 초기화 여부 확인
    private static boolean isHibernateLazyField(Object entity, Field field) {
        try {
            Object value = field.get(entity);

            // PersistentCollection 체크 (OneToMany, ManyToMany 등)
            if (value != null && value.getClass().getName().contains("PersistentCollection")) {
                return !Hibernate.isInitialized(value);
            }

            // HibernateProxy 체크 (ManyToOne, OneToOne 등)
            if (value instanceof HibernateProxy) {
                return !Hibernate.isInitialized(value);
            }

            return false;
        } catch (Exception e) {
            // 접근 불가능한 필드는 Lazy로 간주
            return true;
        }
    }
}