package com.lineinc.erp.api.server.shared.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileMimeType {
    // 이미지 파일들
    JPG("image/jpeg", ".jpg"),
    JPEG("image/jpeg", ".jpeg"),
    PNG("image/png", ".png"),
    GIF("image/gif", ".gif"),
    WEBP("image/webp", ".webp"),
    SVG("image/svg+xml", ".svg"),
    BMP("image/bmp", ".bmp"),
    TIFF("image/tiff", ".tiff"),
    ICO("image/x-icon", ".ico"),

    // 문서 파일들
    PDF("application/pdf", ".pdf"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    XLS("application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    PPT("application/vnd.ms-powerpoint", ".ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
    HWP("application/x-hwp", ".hwp"),
    HWPX("application/vnd.hancom.hwp", ".hwpx"),

    // 텍스트 파일들
    TXT("text/plain", ".txt"),
    CSV("text/csv", ".csv"),
    RTF("application/rtf", ".rtf"),
    HTML("text/html", ".html"),
    HTM("text/html", ".htm"),
    CSS("text/css", ".css"),
    XML("application/xml", ".xml"),
    JSON("application/json", ".json"),

    // 압축 파일들 (안전한 것들만)
    ZIP("application/zip", ".zip"),
    TAR("application/x-tar", ".tar"),
    GZ("application/gzip", ".gz"),

    // 오디오 파일들
    MP3("audio/mpeg", ".mp3"),
    WAV("audio/wav", ".wav"),
    OGG("audio/ogg", ".ogg"),
    M4A("audio/mp4", ".m4a"),

    // 비디오 파일들
    MP4("video/mp4", ".mp4"),
    AVI("video/x-msvideo", ".avi"),
    MOV("video/quicktime", ".mov"),
    WMV("video/x-ms-wmv", ".wmv"),
    WEBM("video/webm", ".webm"),

    // 기타 안전한 파일들
    EML("message/rfc822", ".eml"),
    MSG("application/vnd.ms-outlook", ".msg");

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