package com.lineinc.erp.api.server.shared.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaversUtils {
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
                .filter(vc -> vc.getLeft() != null && vc.getRight() != null)
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
        if (entity instanceof ClientCompanyContact contact) {
            return contact.getName();
        } else if (entity instanceof ClientCompanyFile file) {
            return file.getName();
        } else if (entity instanceof SiteContract contract) {
            return contract.getName();
        } else if (entity instanceof SiteFile file) {
            return file.getName();
        } else if (entity instanceof OutsourcingCompanyContact contact) {
            return contact.getName();
        } else if (entity instanceof OutsourcingCompanyFile file) {
            return file.getName();
        } else if (entity instanceof OutsourcingCompanyContractContact contact) {
            return contact.getName();
        } else if (entity instanceof OutsourcingCompanyContractFile file) {
            return file.getName();
        } else if (entity instanceof OutsourcingCompanyContractWorker worker) {
            return worker.getName();
        } else if (entity instanceof OutsourcingCompanyContractWorkerFile file) {
            return file.getOriginalFileName();
        } else if (entity instanceof OutsourcingCompanyContractEquipment equipment) {
            return equipment.getSpecification();
        } else if (entity instanceof OutsourcingCompanyContractSubEquipment subEquipment) {
            return subEquipment.getDescription();
        }
        return null;
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
        if (entity instanceof ClientCompanyContact contact) {
            return contact.getName() + "(" + contact.getId() + ")";
        } else if (entity instanceof ClientCompanyFile file) {
            return file.getName() + "(" + file.getId() + ")";
        } else if (entity instanceof SiteContract contract) {
            return contract.getName() + "(" + contract.getId() + ")";
        } else if (entity instanceof SiteFile file) {
            return file.getName() + "(" + file.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContact contact) {
            return contact.getName() + "(" + contact.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyFile file) {
            return file.getName() + "(" + file.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractContact contact) {
            return contact.getName() + "(" + contact.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractFile file) {
            return file.getName() + "(" + file.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractWorker worker) {
            return worker.getName() + "(" + worker.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractWorkerFile file) {
            return file.getOriginalFileName() + "(" + file.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractEquipment equipment) {
            return equipment.getSpecification() + "(" + equipment.getId() + ")";
        } else if (entity instanceof OutsourcingCompanyContractSubEquipment subEquipment) {
            return subEquipment.getDescription() + "(" + subEquipment.getId() + ")";
        }
        return null;
    }
}