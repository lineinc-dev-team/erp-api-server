package com.lineinc.erp.api.server.shared.util;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExportUtils {

    @FunctionalInterface
    public interface ExcelHeaderResolver {
        String resolve(String field);
    }

    @FunctionalInterface
    public interface ExcelCellValueExtractor<T> {
        String extract(T item, String field);
    }

    /**
     * 동적 필드 기반 엑셀 생성
     *
     * @param data               데이터 리스트
     * @param fields             출력할 필드 리스트
     * @param headerResolver     필드명 -> 헤더명 매핑 함수
     * @param cellValueExtractor 데이터 항목 + 필드명 -> 셀 값 추출 함수
     * @param <T>                데이터 타입
     * @return 생성된 Workbook
     */
    public static <T> Workbook generateWorkbook(
            final List<T> data,
            final List<String> fields,
            final ExcelHeaderResolver headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Sheet1");

        // id 필드가 있는지 확인
        final boolean hasIdField = fields.contains("id");

        // 1. 헤더 생성
        final Row headerRow = sheet.createRow(0);
        int col = 0;

        // id 필드가 있으면 No. 컬럼 추가
        if (hasIdField) {
            headerRow.createCell(col++).setCellValue("No.");
        }

        for (final String field : fields) {
            if ("id".equals(field))
                continue;
            final String resolvedHeader = headerResolver.resolve(field);
            headerRow.createCell(col++).setCellValue(resolvedHeader != null ? resolvedHeader : field);
        }

        // 2. 데이터 생성
        int rowIdx = 1;
        final int totalCount = data.size();
        for (final T item : data) {
            final Row row = sheet.createRow(rowIdx);
            col = 0;

            // id 필드가 있으면 No. 컬럼 추가 (역순)
            if (hasIdField) {
                row.createCell(col++).setCellValue(String.valueOf(totalCount - rowIdx + 1));
            }

            for (final String field : fields) {
                if ("id".equals(field))
                    continue;
                final String cellValue = cellValueExtractor.extract(item, field);
                row.createCell(col++).setCellValue(cellValue != null ? cellValue : "");
            }
            rowIdx++;
        }

        return workbook;
    }
}