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
                .map(vc -> Map.of(vc.getPropertyName(), vc.getLeft() + " ⇒ " + vc.getRight()))
                .collect(Collectors.toList());
    }
}