package com.lineinc.erp.api.server.shared.util;

import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExportUtils {

    /**
     * 숫자 타입으로 저장해야 하는 필드명인지 확인
     * 엔티티 분석 결과를 바탕으로 실제 사용되는 숫자 관련 필드만 true 반환
     */
    private static boolean isNumericField(final String field) {
        // 정확한 필드명 매칭 (가장 일반적인 숫자 필드들)
        if (field.matches(
                "(quantity|count|weight|amount|price|cost|vat|total|rate|percent|fee|deduction)")) {
            return true;
        }

        // 접두사/접미사 포함 패턴 (예: unitPrice, supplyPrice, totalAmount 등)
        if (field.matches(
                ".*(Quantity|Count|Weight|Amount|Price|Cost|Vat|Total|Rate|Percent|Fee|Deduction)")) {
            return true;
        }

        // 특수 케이스: 대문자로 시작하는 약어
        if (field.matches(".*(VAT).*")) {
            return true;
        }

        return false;
    }

    /**
     * 숫자 셀 스타일 생성 (오른쪽 정렬 + 천단위 구분 기호)
     */
    private static CellStyle createNumberStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    /**
     * 소수점 숫자 셀 스타일 생성 (오른쪽 정렬 + 천단위 구분 기호 + 소수점)
     */
    private static CellStyle createDecimalStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0#"));
        return style;
    }

    @FunctionalInterface
    public interface ExcelHeaderResolver<T> {
        String resolve(String field, T context);
    }

    @FunctionalInterface
    public interface ExcelHeaderResolverSimple {
        String resolve(String field);
    }

    @FunctionalInterface
    public interface ExcelCellValueExtractor<T> {
        String extract(T item, String field);
    }

    @FunctionalInterface
    public interface ExcelSheetNameExtractor<K> {
        String extract(K key);
    }

    /**
     * 동적 필드 기반 엑셀 생성 (기존 호환성 유지)
     */
    public static <T> Workbook generateWorkbook(final List<T> data, final List<String> fields,
            final ExcelHeaderResolverSimple headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Sheet1");
        createSheetWithDataSimple(sheet, data, fields, headerResolver, cellValueExtractor, false, 0,
                List.of());
        return workbook;
    }

    /**
     * 동적 필드 기반 엑셀 생성 (컨텍스트 지원)
     */
    public static <T, K> Workbook generateWorkbookWithContext(final List<T> data,
            final List<String> fields, final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor, final K context) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Sheet1");
        createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, false, 0,
                List.of(), context);
        return workbook;
    }

    /**
     * 여러 시트를 가진 동적 필드 기반 엑셀 생성
     */
    public static <T, K> Workbook generateMultiSheetWorkbook(final Map<K, List<T>> dataBySheet,
            final List<String> fields, final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final ExcelSheetNameExtractor<K> sheetNameExtractor) {
        final Workbook workbook = new XSSFWorkbook();

        for (final Map.Entry<K, List<T>> entry : dataBySheet.entrySet()) {
            final K sheetKey = entry.getKey();
            final List<T> data = entry.getValue();

            if (data.isEmpty()) {
                continue; // 빈 데이터는 시트를 생성하지 않음
            }

            final String sheetName = sheetNameExtractor.extract(sheetKey);
            final Sheet sheet = workbook.createSheet(sheetName);
            createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, false, 0,
                    List.of(), sheetKey);
        }

        return workbook;
    }

    /**
     * 여러 시트를 가진 동적 필드 기반 엑셀 생성 (소계 포함)
     */
    public static <T, K> Workbook generateMultiSheetWorkbookWithSubtotal(
            final Map<K, List<T>> dataBySheet, final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final ExcelSheetNameExtractor<K> sheetNameExtractor, final int subtotalMergeColumns,
            final List<String> subtotalFields) {
        final Workbook workbook = new XSSFWorkbook();

        for (final Map.Entry<K, List<T>> entry : dataBySheet.entrySet()) {
            final K sheetKey = entry.getKey();
            final List<T> data = entry.getValue();

            if (data.isEmpty()) {
                continue; // 빈 데이터는 시트를 생성하지 않음
            }

            final String sheetName = sheetNameExtractor.extract(sheetKey);
            final Sheet sheet = workbook.createSheet(sheetName);
            createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, true,
                    subtotalMergeColumns, subtotalFields, sheetKey);
        }

        return workbook;
    }

    /**
     * 시트에 데이터를 생성하는 공통 메서드 (기존 호환성 유지)
     */
    private static <T> void createSheetWithDataSimple(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelHeaderResolverSimple headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor, final boolean includeSubtotal,
            final int subtotalMergeColumns, final List<String> subtotalFields) {
        final boolean hasIdField = fields.contains("id");

        // 1. 헤더 생성
        createHeaderRowSimple(sheet, fields, headerResolver, hasIdField);

        // 2. 데이터 생성
        final int rowIdx = createDataRowsSimple(sheet, data, fields, cellValueExtractor, hasIdField,
                headerResolver);

        // 3. 소계 행 생성 (includeSubtotal이 true인 경우에만)
        if (includeSubtotal) {
            createSubtotalRowSimple(sheet, data, fields, cellValueExtractor, hasIdField, rowIdx,
                    subtotalMergeColumns, subtotalFields, headerResolver);
        }
    }

    /**
     * 시트에 데이터를 생성하는 공통 메서드 (컨텍스트 지원)
     */
    private static <T, K> void createSheetWithData(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor, final boolean includeSubtotal,
            final int subtotalMergeColumns, final List<String> subtotalFields, final K context) {
        final boolean hasIdField = fields.contains("id");

        // 1. 헤더 생성
        createHeaderRow(sheet, fields, headerResolver, hasIdField, context);

        // 2. 데이터 생성
        final int rowIdx = createDataRows(sheet, data, fields, cellValueExtractor, hasIdField,
                headerResolver, context);

        // 3. 소계 행 생성 (includeSubtotal이 true인 경우에만)
        if (includeSubtotal) {
            createSubtotalRow(sheet, data, fields, cellValueExtractor, hasIdField, rowIdx,
                    subtotalMergeColumns, subtotalFields, headerResolver, context);
        }
    }

    /**
     * 헤더 행 생성 (기존 호환성 유지)
     */
    private static void createHeaderRowSimple(final Sheet sheet, final List<String> fields,
            final ExcelHeaderResolverSimple headerResolver, final boolean hasIdField) {
        final Row headerRow = sheet.createRow(0);
        int col = 0;

        if (hasIdField) {
            headerRow.createCell(col++).setCellValue("No.");
        }

        for (final String field : fields) {
            if ("id".equals(field))
                continue;
            final String resolvedHeader = headerResolver.resolve(field);
            headerRow.createCell(col++)
                    .setCellValue(resolvedHeader != null ? resolvedHeader : field);
        }
    }

    /**
     * 헤더 행 생성 (컨텍스트 지원)
     */
    private static <K> void createHeaderRow(final Sheet sheet, final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver, final boolean hasIdField,
            final K context) {
        final Row headerRow = sheet.createRow(0);
        int col = 0;

        if (hasIdField) {
            headerRow.createCell(col++).setCellValue("No.");
        }

        for (final String field : fields) {
            if ("id".equals(field))
                continue;
            final String resolvedHeader = headerResolver.resolve(field, context);
            // null인 헤더는 해당 컬럼을 제외
            if (resolvedHeader != null) {
                headerRow.createCell(col++).setCellValue(resolvedHeader);
            }
        }
    }

    /**
     * 데이터 행들 생성 (기존 호환성 유지)
     */
    private static <T> int createDataRowsSimple(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField, final ExcelHeaderResolverSimple headerResolver) {
        int rowIdx = 1;
        final int totalCount = data.size();

        // 스타일 생성
        final CellStyle numberStyle = createNumberStyle(sheet.getWorkbook());
        final CellStyle decimalStyle = createDecimalStyle(sheet.getWorkbook());

        for (final T item : data) {
            final Row row = sheet.createRow(rowIdx);
            int col = 0;

            if (hasIdField) {
                row.createCell(col++).setCellValue(String.valueOf(totalCount - rowIdx + 1));
            }

            for (final String field : fields) {
                if ("id".equals(field))
                    continue;

                final String cellValue = cellValueExtractor.extract(item, field);
                final var cell = row.createCell(col++);

                setCellValueWithFormat(cell, cellValue, field, numberStyle, decimalStyle);
            }
            rowIdx++;
        }

        return rowIdx;
    }

    /**
     * 셀 값 설정 및 포맷 적용 (숫자 필드 판단 포함)
     */
    private static void setCellValueWithFormat(final org.apache.poi.ss.usermodel.Cell cell,
            final String cellValue, final String field, final CellStyle numberStyle,
            final CellStyle decimalStyle) {
        if (cellValue == null || cellValue.isEmpty()) {
            cell.setCellValue("");
            return;
        }

        // 필드명이 숫자 관련 필드인 경우에만 숫자 타입으로 저장 시도
        if (isNumericField(field)) {
            try {
                final double numericValue = Double.parseDouble(cellValue.replaceAll(",", ""));
                cell.setCellValue(numericValue);

                // 소수점이 있으면 소수점 스타일, 없으면 정수 스타일 적용
                if (cellValue.contains(".") || numericValue % 1 != 0) {
                    cell.setCellStyle(decimalStyle);
                } else {
                    cell.setCellStyle(numberStyle);
                }
            } catch (final NumberFormatException e) {
                // 숫자로 파싱 실패하면 문자열로 저장
                cell.setCellValue(cellValue);
            }
        } else {
            // 숫자 필드가 아니면 문자열로 저장
            cell.setCellValue(cellValue);
        }
    }

    /**
     * 데이터 행들 생성 (컨텍스트 지원)
     */
    private static <T, K> int createDataRows(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField, final ExcelHeaderResolver<K> headerResolver,
            final K context) {
        int rowIdx = 1;
        final int totalCount = data.size();

        // 스타일 생성
        final CellStyle numberStyle = createNumberStyle(sheet.getWorkbook());
        final CellStyle decimalStyle = createDecimalStyle(sheet.getWorkbook());

        for (final T item : data) {
            final Row row = sheet.createRow(rowIdx);
            int col = 0;

            if (hasIdField) {
                row.createCell(col++).setCellValue(String.valueOf(totalCount - rowIdx + 1));
            }

            for (final String field : fields) {
                if ("id".equals(field))
                    continue;

                final String resolvedHeader = headerResolver.resolve(field, context);
                // null인 헤더는 해당 컬럼을 제외
                if (resolvedHeader != null) {
                    final String cellValue = cellValueExtractor.extract(item, field);
                    final var cell = row.createCell(col++);

                    setCellValueWithFormat(cell, cellValue, field, numberStyle, decimalStyle);
                }
            }
            rowIdx++;
        }

        return rowIdx;
    }

    /**
     * 소계 행 생성 (기존 호환성 유지)
     */
    private static <T> void createSubtotalRowSimple(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField, final int rowIdx, final int subtotalMergeColumns,
            final List<String> subtotalFields, final ExcelHeaderResolverSimple headerResolver) {
        final Row subtotalRow = sheet.createRow(rowIdx);

        // 소계 텍스트를 첫 번째 셀에 표시하고 가운데 정렬
        final var subtotalCell = subtotalRow.createCell(0);
        subtotalCell.setCellValue("소계");

        // 가운데 정렬 스타일 적용
        final CellStyle centerAlignStyle = sheet.getWorkbook().createCellStyle();
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        subtotalCell.setCellStyle(centerAlignStyle);

        // 숫자 스타일 생성
        final CellStyle numberStyle = createNumberStyle(sheet.getWorkbook());

        // 병합할 컬럼 수만큼 건너뛰기
        int col = subtotalMergeColumns;

        for (final String field : fields) {
            if ("id".equals(field))
                continue;

            // 지정된 필드만 소계에 표시
            if (subtotalFields.contains(field)) {
                final double sum = data.stream().mapToDouble(item -> {
                    try {
                        final String value = cellValueExtractor.extract(item, field);
                        return value != null && !value.isEmpty()
                                ? Double.parseDouble(value.replaceAll(",", ""))
                                : 0.0;
                    } catch (final NumberFormatException e) {
                        return 0.0;
                    }
                }).sum();

                final var cell = subtotalRow.createCell(col++);
                cell.setCellValue(sum);
                cell.setCellStyle(numberStyle);
            }
        }

        // 소계 텍스트가 차지할 컬럼 수만큼 병합
        if (subtotalMergeColumns > 1) {
            final CellRangeAddress mergedRegion =
                    new CellRangeAddress(rowIdx, rowIdx, 0, subtotalMergeColumns - 1);
            sheet.addMergedRegion(mergedRegion);
        }
    }

    /**
     * 소계 행 생성 (컨텍스트 지원)
     */
    private static <T, K> void createSubtotalRow(final Sheet sheet, final List<T> data,
            final List<String> fields, final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField, final int rowIdx, final int subtotalMergeColumns,
            final List<String> subtotalFields, final ExcelHeaderResolver<K> headerResolver,
            final K context) {
        final Row subtotalRow = sheet.createRow(rowIdx);

        // 소계 텍스트를 첫 번째 셀에 표시하고 가운데 정렬
        final var subtotalCell = subtotalRow.createCell(0);
        subtotalCell.setCellValue("소계");

        // 가운데 정렬 스타일 적용
        final CellStyle centerAlignStyle = sheet.getWorkbook().createCellStyle();
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        subtotalCell.setCellStyle(centerAlignStyle);

        // 숫자 스타일 생성
        final CellStyle numberStyle = createNumberStyle(sheet.getWorkbook());

        // 병합할 컬럼 수만큼 건너뛰기
        int col = subtotalMergeColumns;

        for (final String field : fields) {
            if ("id".equals(field))
                continue;

            final String resolvedHeader = headerResolver.resolve(field, context);
            // null인 헤더는 해당 컬럼을 제외
            if (resolvedHeader == null) {
                continue;
            }

            // 지정된 필드만 소계에 표시
            if (subtotalFields.contains(field)) {
                final double sum = data.stream().mapToDouble(item -> {
                    try {
                        final String value = cellValueExtractor.extract(item, field);
                        return value != null && !value.isEmpty()
                                ? Double.parseDouble(value.replaceAll(",", ""))
                                : 0.0;
                    } catch (final NumberFormatException e) {
                        return 0.0;
                    }
                }).sum();

                final var cell = subtotalRow.createCell(col++);
                cell.setCellValue(sum);
                cell.setCellStyle(numberStyle);
            }
        }

        // 소계 텍스트가 차지할 컬럼 수만큼 병합
        if (subtotalMergeColumns > 1) {
            final CellRangeAddress mergedRegion =
                    new CellRangeAddress(rowIdx, rowIdx, 0, subtotalMergeColumns - 1);
            sheet.addMergedRegion(mergedRegion);
        }
    }

}
