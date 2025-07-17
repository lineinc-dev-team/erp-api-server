package com.lineinc.erp.api.server.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileMimeType {
    JPG("image/jpeg", ".jpg"),
    PNG("image/png", ".png"),
    PDF("application/pdf", ".pdf"),
    HWP("application/x-hwp", ".hwp"),
    ZIP("application/zip", ".zip"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    XLS("application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");

    private final String mime;
    private final String extension;

    FileMimeType(String mime, String extension) {
        this.mime = mime;
        this.extension = extension;
    }

    public static boolean isSupported(String input) {
        return Arrays.stream(values()).anyMatch(f -> f.mime.equals(input));
    }

    public static FileMimeType fromMime(String input) {
        return Arrays.stream(values())
                .filter(f -> f.mime.equals(input))
                .findFirst()
                .orElse(null);
    }
}