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
        String afterValue;
        if (newEntity instanceof ClientCompanyContact clientCompanyContact) {
            afterValue = clientCompanyContact.getName();
        } else if (newEntity instanceof ClientCompanyFile clientCompanyFile) {
            afterValue = clientCompanyFile.getName();
        } else if (newEntity instanceof SiteContract siteContract) {
            afterValue = siteContract.getName();
        } else if (newEntity instanceof SiteFile siteFile) {
            afterValue = siteFile.getName();
        } else if (newEntity instanceof OutsourcingCompanyContact outsourcingCompanyContact) {
            afterValue = outsourcingCompanyContact.getName();
        } else if (newEntity instanceof OutsourcingCompanyFile outsourcingCompanyFile) {
            afterValue = outsourcingCompanyFile.getName();
        } else {
            afterValue = toJsonSafe(javers, newEntity);
        }

        return Map.of(
                "property", "null",
                "before", "null",
                "after", afterValue,
                "type", "추가");
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
        if (affectedObject instanceof ClientCompanyContact clientCompanyContact) {
            return clientCompanyContact.getName() + "(" + clientCompanyContact.getId() + ")";
        } else if (affectedObject instanceof ClientCompanyFile clientCompanyFile) {
            return clientCompanyFile.getName() + "(" + clientCompanyFile.getId() + ")";
        } else if (affectedObject instanceof SiteContract siteContract) {
            return siteContract.getName() + "(" + siteContract.getId() + ")";
        } else if (affectedObject instanceof SiteFile siteFile) {
            return siteFile.getName() + "(" + siteFile.getId() + ")";
        } else if (affectedObject instanceof OutsourcingCompanyContact outsourcingCompanyContact) {
            return outsourcingCompanyContact.getName() + "(" + outsourcingCompanyContact.getId() + ")";
        } else if (affectedObject instanceof OutsourcingCompanyFile outsourcingCompanyFile) {
            return outsourcingCompanyFile.getName() + "(" + outsourcingCompanyFile.getId() + ")";
        } else if (affectedObject != null) {
            return affectedObject.toString();
        } else {
            return "";
        }
    }
}