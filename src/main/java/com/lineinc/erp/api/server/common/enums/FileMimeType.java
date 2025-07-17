package com.lineinc.erp.api.server.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileMimeType {
    JPG("image/jpeg"),
    PNG("image/png"),
    PDF("application/pdf"),
    HWP("application/x-hwp"),
    ZIP("application/zip"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String mime;

    FileMimeType(String mime) {
        this.mime = mime;
    }

    public static boolean isSupported(String input) {
        return Arrays.stream(values()).anyMatch(f -> f.mime.equals(input));
    }
}