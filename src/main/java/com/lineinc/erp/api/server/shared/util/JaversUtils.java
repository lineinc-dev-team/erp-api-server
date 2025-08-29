package com.lineinc.erp.api.server.shared.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriverFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaversUtils {

    // 엔티티 타입별 이름 추출 함수를 미리 정의
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
                    entity -> ((OutsourcingCompanyContractSubEquipment) entity).getDescription()),
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

    public static <T> T createSnapshot(Javers javers, T entity, Class<T> clazz) {
        try {
            String json = javers.getJsonConverter().toJson(entity);
            return javers.getJsonConverter().fromJson(json, clazz);
        } catch (Exception e) {
            log.error("❌ createSnapshot 직렬화 실패 클래스: {}", entity.getClass().getName());
            try {
                var fields = entity.getClass().getDeclaredFields();
                for (var field : fields) {
                    field.setAccessible(true);
                    Object value = null;
                    try {
                        value = field.get(entity);
                    } catch (Exception reflectionEx) {
                        continue;
                    }
                    try {
                        javers.getJsonConverter().toJson(value);
                    } catch (Exception innerEx) {
                        log.error("     ✖ 직렬화 실패 프로퍼티: {}", field.getName());
                    }
                }
            } catch (Exception reflectionEx) {
                // ignore reflection exceptions here
            }
            throw e;
        }
    }

    public static List<Map<String, String>> extractModifiedChanges(Javers javers, Diff diff) {
        return diff.getChanges().stream()
                .filter(change -> change instanceof ValueChange)
                .map(change -> (ValueChange) change)
                .map(vc -> {
                    if ("deleted".equals(vc.getPropertyName())
                            && Boolean.FALSE.equals(vc.getLeft())
                            && Boolean.TRUE.equals(vc.getRight())) {
                        String beforeValue = formatDeletedContactName(vc.getAffectedObject());

                        return Map.of(
                                "property", "null",
                                "before", beforeValue,
                                "after", "null",
                                "type", "삭제");
                    } else {
                        String changeType = "수정";

                        return Map.of(
                                "property", vc.getPropertyName(),
                                "before", toJsonSafe(javers, vc.getLeft()),
                                "after", toJsonSafe(javers, vc.getRight()),
                                "type", changeType);
                    }
                })
                .collect(Collectors.toList());
    }

    // 단일 추가 엔티티 변경 내역 추출
    public static Map<String, String> extractAddedEntityChange(Javers javers, Object newEntity) {
        String afterValue = extractEntityName(newEntity);
        if (afterValue == null) {
            afterValue = toJsonSafe(javers, newEntity);
        }

        return Map.of(
                "property", "null",
                "before", "null",
                "after", afterValue,
                "type", "추가");
    }

    private static String extractEntityName(Object entity) {
        if (entity == null)
            return null;

        log.debug("엔티티 타입: {}", entity.getClass().getSimpleName());

        // 엔티티 타입에 맞는 이름 추출 함수 찾기
        Function<Object, String> extractor = ENTITY_NAME_EXTRACTORS.get(entity.getClass());
        if (extractor != null) {
            try {
                return extractor.apply(entity);
            } catch (Exception e) {
                log.warn("엔티티 이름 추출 실패: {}", entity.getClass().getSimpleName(), e);
                return null;
            }
        }

        return null;
    }

    /**
     * 엔티티에서 ID를 추출하는 공통 메서드
     */
    private static Long extractEntityId(Object entity) {
        if (entity == null)
            return null;

        try {
            // getId() 메서드가 있는 경우
            var getIdMethod = entity.getClass().getMethod("getId");
            Object result = getIdMethod.invoke(entity);
            return result instanceof Long ? (Long) result : null;
        } catch (Exception e) {
            try {
                // id() 메서드가 있는 경우 (record 클래스용)
                var idMethod = entity.getClass().getMethod("id");
                Object result = idMethod.invoke(entity);
                return result instanceof Long ? (Long) result : null;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private static String toJsonSafe(Javers javers, Object obj) {
        if (obj == null)
            return "";
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Character) {
            return obj.toString();
        }
        return javers.getJsonConverter().toJson(obj);
    }

    private static String formatDeletedContactName(Object affectedObject) {
        if (affectedObject instanceof Optional<?> optional) {
            affectedObject = optional.orElse(null);
        }

        String entityName = extractEntityNameWithId(affectedObject);
        if (entityName != null) {
            return entityName;
        }

        return affectedObject != null ? affectedObject.toString() : "";
    }

    private static String extractEntityNameWithId(Object entity) {
        if (entity == null)
            return null;

        // 기존 Map을 활용하여 이름 추출
        String name = extractEntityName(entity);
        if (name == null)
            return null;

        // ID 추출
        Long id = extractEntityId(entity);
        if (id == null)
            return name; // ID가 없어도 이름은 반환

        return name + "(" + id + ")";
    }
}