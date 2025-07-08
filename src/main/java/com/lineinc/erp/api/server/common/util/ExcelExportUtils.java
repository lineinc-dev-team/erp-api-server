package com.lineinc.erp.api.server.common.util;

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
     * @param sheetName          시트명
     * @param data               데이터 리스트
     * @param fields             출력할 필드 리스트
     * @param headerResolver     필드명 -> 헤더명 매핑 함수
     * @param cellValueExtractor 데이터 항목 + 필드명 -> 셀 값 추출 함수
     * @param <T>                데이터 타입
     * @return 생성된 Workbook
     */
    public static <T> Workbook generateWorkbook(
            String sheetName,
            List<T> data,
            List<String> fields,
            ExcelHeaderResolver headerResolver,
            ExcelCellValueExtractor<T> cellValueExtractor
    ) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 1. 헤더 생성
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i).setCellValue(headerResolver.resolve(fields.get(i)));
        }

        // 2. 데이터 생성
        int rowIdx = 1;
        for (T item : data) {
            Row row = sheet.createRow(rowIdx++);
            for (int i = 0; i < fields.size(); i++) {
                row.createCell(i).setCellValue(cellValueExtractor.extract(item, fields.get(i)));
            }
        }

        return workbook;
    }
}