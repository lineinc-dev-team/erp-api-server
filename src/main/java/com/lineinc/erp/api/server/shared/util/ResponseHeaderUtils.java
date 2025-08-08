package com.lineinc.erp.api.server.shared.util;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ResponseHeaderUtils {

    /**
     * 엑셀 다운로드용 응답 헤더를 설정합니다.
     *
     * @param response HttpServletResponse 객체
     * @param filename 다운로드할 파일명 (예: client_companies.xlsx)
     */
    public static void setExcelDownloadHeader(HttpServletResponse response, String filename) {
        // MIME 타입을 엑셀 문서 형식으로 지정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try {
            // 파일명을 UTF-8로 URL 인코딩합니다.
            // URLEncoder.encode는 공백을 '+'로 인코딩하는데,
            // HTTP 헤더에서는 공백을 '%20'으로 인코딩해야 하므로 replaceAll로 변환합니다.
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            // RFC 5987 표준에 따라 Content-Disposition 헤더를 설정합니다.
            // filename*=UTF-8'' 뒤에 인코딩된 파일명이 오며,
            // 이는 브라우저가 UTF-8 인코딩된 파일명을 올바르게 처리하도록 합니다.
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFilename;

            // 인코딩된 파일명으로 Content-Disposition 헤더를 설정하여,
            // 브라우저가 다운로드 시 올바른 파일명으로 저장하도록 합니다.
            response.setHeader("Content-Disposition", contentDisposition);
        } catch (Exception e) {
            // 인코딩 실패 시 안전한 기본 파일명 사용
            response.setHeader("Content-Disposition", "attachment; filename=download.xlsx");
        }
    }
}