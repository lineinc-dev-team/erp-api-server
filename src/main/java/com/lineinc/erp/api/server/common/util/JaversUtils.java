package com.lineinc.erp.api.server.common.util;

import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.Javers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JaversUtils {

    public static <T> T createSnapshot(Javers javers, T entity, Class<T> clazz) {
        return javers.getJsonConverter()
                .fromJson(javers.getJsonConverter().toJson(entity), clazz);
    }

    public static List<Map<String, String>> extractSimpleChanges(Diff diff) {
        return diff.getChanges().stream()
                .filter(change -> change instanceof ValueChange)
                .map(change -> (ValueChange) change)
                .map(vc -> {
                    String changeType;
                    if (vc.getLeft() == null && vc.getRight() != null) {
                        changeType = "추가";
                    } else if (vc.getLeft() != null && vc.getRight() == null) {
                        changeType = "삭제";
                    } else {
                        changeType = "수정";
                    }

                    return Map.of(
                            "property", vc.getPropertyName(),
                            "before", vc.getLeft() == null ? "" : vc.getLeft().toString(),
                            "after", vc.getRight() == null ? "" : vc.getRight().toString(),
                            "type", changeType
                    );
                })
                .collect(Collectors.toList());
    }
}