package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.Javers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JaversUtils {

    public static <T> T createSnapshot(Javers javers, T entity, Class<T> clazz) {
        return javers.getJsonConverter()
                .fromJson(javers.getJsonConverter().toJson(entity), clazz);
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
                                "type", "삭제"
                        );
                    } else {
                        String changeType = "수정";

                        return Map.of(
                                "property", vc.getPropertyName(),
                                "before", toJsonSafe(javers, vc.getLeft()),
                                "after", toJsonSafe(javers, vc.getRight()),
                                "type", changeType
                        );
                    }
                })
                .collect(Collectors.toList());
    }

    // 단일 추가 엔티티 변경 내역 추출
    public static Map<String, String> extractAddedEntityChange(Javers javers, Object newEntity) {
        String afterValue;
        if (newEntity instanceof ClientCompanyContact contact) {
            afterValue = contact.getName();
        } else if (newEntity instanceof ClientCompanyFile file) {
            afterValue = file.getName();
        } else {
            afterValue = toJsonSafe(javers, newEntity);
        }

        return Map.of(
                "property", "null",
                "before", "null",
                "after", afterValue,
                "type", "추가"
        );
    }

    private static String toJsonSafe(Javers javers, Object obj) {
        if (obj == null) return "";
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Character) {
            return obj.toString();
        }
        return javers.getJsonConverter().toJson(obj);
    }

    private static String formatDeletedContactName(Object affectedObject) {
        if (affectedObject instanceof Optional<?> optional) {
            affectedObject = optional.orElse(null);
        }
        if (affectedObject instanceof ClientCompanyContact contact) {
            return contact.getName() + "(" + contact.getId() + ")";
        } else if (affectedObject instanceof ClientCompanyFile file) {
            return file.getName() + "(" + file.getId() + ")";
        } else if (affectedObject != null) {
            return affectedObject.toString();
        } else {
            return "";
        }
    }
}