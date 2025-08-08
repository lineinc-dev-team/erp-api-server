package com.lineinc.erp.api.server.shared.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

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
            List<T> data,
            List<String> fields,
            ExcelHeaderResolver headerResolver,
            ExcelCellValueExtractor<T> cellValueExtractor
    ) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 1. 헤더 생성 (첫 번째 컬럼은 무조건 "No.")
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("No.");
        for (int i = 0, col = 1; i < fields.size(); i++) {
            String field = fields.get(i);
            if ("id".equals(field)) continue;
            String resolvedHeader = headerResolver.resolve(field);
            headerRow.createCell(col++).setCellValue(resolvedHeader != null ? resolvedHeader : field);
        }

        // 2. 데이터 생성 (첫 번째 컬럼에 행 번호 삽입)
        int rowIdx = 1;
        for (T item : data) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue(String.valueOf(rowIdx));
            int col = 1;
            for (String field : fields) {
                if ("id".equals(field)) continue;
                String cellValue = cellValueExtractor.extract(item, field);
                row.createCell(col++).setCellValue(cellValue != null ? cellValue : "");
            }
            rowIdx++;
        }

        return workbook;
    }
}