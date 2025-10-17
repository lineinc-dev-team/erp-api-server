package com.lineinc.erp.api.server.shared.util;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExportUtils {

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
    public static <T> Workbook generateWorkbook(
            final List<T> data,
            final List<String> fields,
            final ExcelHeaderResolverSimple headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Sheet1");
        createSheetWithDataSimple(sheet, data, fields, headerResolver, cellValueExtractor, false, 0, List.of());
        return workbook;
    }

    /**
     * 동적 필드 기반 엑셀 생성 (컨텍스트 지원)
     */
    public static <T, K> Workbook generateWorkbookWithContext(
            final List<T> data,
            final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final K context) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Sheet1");
        createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, false, 0, List.of(), context);
        return workbook;
    }

    /**
     * 여러 시트를 가진 동적 필드 기반 엑셀 생성
     */
    public static <T, K> Workbook generateMultiSheetWorkbook(
            final Map<K, List<T>> dataBySheet,
            final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
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
            createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, false, 0, List.of(), sheetKey);
        }

        return workbook;
    }

    /**
     * 여러 시트를 가진 동적 필드 기반 엑셀 생성 (소계 포함)
     */
    public static <T, K> Workbook generateMultiSheetWorkbookWithSubtotal(
            final Map<K, List<T>> dataBySheet,
            final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final ExcelSheetNameExtractor<K> sheetNameExtractor,
            final int subtotalMergeColumns,
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
            createSheetWithData(sheet, data, fields, headerResolver, cellValueExtractor, true, subtotalMergeColumns,
                    subtotalFields, sheetKey);
        }

        return workbook;
    }

    /**
     * 시트에 데이터를 생성하는 공통 메서드 (기존 호환성 유지)
     */
    private static <T> void createSheetWithDataSimple(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelHeaderResolverSimple headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean includeSubtotal,
            final int subtotalMergeColumns,
            final List<String> subtotalFields) {
        final boolean hasIdField = fields.contains("id");

        // 1. 헤더 생성
        createHeaderRowSimple(sheet, fields, headerResolver, hasIdField);

        // 2. 데이터 생성
        final int rowIdx = createDataRowsSimple(sheet, data, fields, cellValueExtractor, hasIdField, headerResolver);

        // 3. 소계 행 생성 (includeSubtotal이 true인 경우에만)
        if (includeSubtotal) {
            createSubtotalRowSimple(sheet, data, fields, cellValueExtractor, hasIdField, rowIdx, subtotalMergeColumns,
                    subtotalFields, headerResolver);
        }
    }

    /**
     * 시트에 데이터를 생성하는 공통 메서드 (컨텍스트 지원)
     */
    private static <T, K> void createSheetWithData(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean includeSubtotal,
            final int subtotalMergeColumns,
            final List<String> subtotalFields,
            final K context) {
        final boolean hasIdField = fields.contains("id");

        // 1. 헤더 생성
        createHeaderRow(sheet, fields, headerResolver, hasIdField, context);

        // 2. 데이터 생성
        final int rowIdx = createDataRows(sheet, data, fields, cellValueExtractor, hasIdField, headerResolver, context);

        // 3. 소계 행 생성 (includeSubtotal이 true인 경우에만)
        if (includeSubtotal) {
            createSubtotalRow(sheet, data, fields, cellValueExtractor, hasIdField, rowIdx, subtotalMergeColumns,
                    subtotalFields, headerResolver, context);
        }
    }

    /**
     * 헤더 행 생성 (기존 호환성 유지)
     */
    private static void createHeaderRowSimple(
            final Sheet sheet,
            final List<String> fields,
            final ExcelHeaderResolverSimple headerResolver,
            final boolean hasIdField) {
        final Row headerRow = sheet.createRow(0);
        int col = 0;

        if (hasIdField) {
            headerRow.createCell(col++).setCellValue("No.");
        }

        for (final String field : fields) {
            if ("id".equals(field))
                continue;
            final String resolvedHeader = headerResolver.resolve(field);
            headerRow.createCell(col++).setCellValue(resolvedHeader != null ? resolvedHeader : field);
        }
    }

    /**
     * 헤더 행 생성 (컨텍스트 지원)
     */
    private static <K> void createHeaderRow(
            final Sheet sheet,
            final List<String> fields,
            final ExcelHeaderResolver<K> headerResolver,
            final boolean hasIdField,
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
    private static <T> int createDataRowsSimple(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField,
            final ExcelHeaderResolverSimple headerResolver) {
        int rowIdx = 1;
        final int totalCount = data.size();

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
                row.createCell(col++).setCellValue(cellValue != null ? cellValue : "");
            }
            rowIdx++;
        }

        return rowIdx;
    }

    /**
     * 데이터 행들 생성 (컨텍스트 지원)
     */
    private static <T, K> int createDataRows(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField,
            final ExcelHeaderResolver<K> headerResolver,
            final K context) {
        int rowIdx = 1;
        final int totalCount = data.size();

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
                    row.createCell(col++).setCellValue(cellValue != null ? cellValue : "");
                }
            }
            rowIdx++;
        }

        return rowIdx;
    }

    /**
     * 소계 행 생성 (기존 호환성 유지)
     */
    private static <T> void createSubtotalRowSimple(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField,
            final int rowIdx,
            final int subtotalMergeColumns,
            final List<String> subtotalFields,
            final ExcelHeaderResolverSimple headerResolver) {
        final Row subtotalRow = sheet.createRow(rowIdx);
        int col = 0;

        // 소계 텍스트를 첫 번째 셀에 표시하고 가운데 정렬
        final var subtotalCell = subtotalRow.createCell(0);
        subtotalCell.setCellValue("소계");

        // 가운데 정렬 스타일 적용
        final CellStyle centerAlignStyle = sheet.getWorkbook().createCellStyle();
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        subtotalCell.setCellStyle(centerAlignStyle);

        // 병합할 컬럼 수만큼 건너뛰기
        col = subtotalMergeColumns;

        for (final String field : fields) {
            if ("id".equals(field))
                continue;

            // 지정된 필드만 소계에 표시
            if (subtotalFields.contains(field)) {
                final double sum = data.stream()
                        .mapToDouble(item -> {
                            try {
                                final String value = cellValueExtractor.extract(item, field);
                                return value != null && !value.isEmpty()
                                        ? Double.parseDouble(value.replaceAll(",", ""))
                                        : 0.0;
                            } catch (final NumberFormatException e) {
                                return 0.0;
                            }
                        })
                        .sum();

                final var cell = subtotalRow.createCell(col++);

                // 천단위 표기 적용
                final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
                final String formattedSum = numberFormat.format(sum);
                cell.setCellValue(formattedSum);

                // 왼쪽 정렬 스타일 적용
                final CellStyle leftAlignStyle = sheet.getWorkbook().createCellStyle();
                leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellStyle(leftAlignStyle);
            }
            // 지정되지 않은 필드는 소계에서 제외 (빈 셀 생성하지 않음)
        }

        // 소계 텍스트가 차지할 컬럼 수만큼 병합
        if (subtotalMergeColumns > 1) {
            final CellRangeAddress mergedRegion = new CellRangeAddress(rowIdx, rowIdx, 0, subtotalMergeColumns - 1);
            sheet.addMergedRegion(mergedRegion);
        }
    }

    /**
     * 소계 행 생성 (컨텍스트 지원)
     */
    private static <T, K> void createSubtotalRow(
            final Sheet sheet,
            final List<T> data,
            final List<String> fields,
            final ExcelCellValueExtractor<T> cellValueExtractor,
            final boolean hasIdField,
            final int rowIdx,
            final int subtotalMergeColumns,
            final List<String> subtotalFields,
            final ExcelHeaderResolver<K> headerResolver,
            final K context) {
        final Row subtotalRow = sheet.createRow(rowIdx);
        int col = 0;

        // 소계 텍스트를 첫 번째 셀에 표시하고 가운데 정렬
        final var subtotalCell = subtotalRow.createCell(0);
        subtotalCell.setCellValue("소계");

        // 가운데 정렬 스타일 적용
        final CellStyle centerAlignStyle = sheet.getWorkbook().createCellStyle();
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        subtotalCell.setCellStyle(centerAlignStyle);

        // 병합할 컬럼 수만큼 건너뛰기
        col = subtotalMergeColumns;

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
                final double sum = data.stream()
                        .mapToDouble(item -> {
                            try {
                                final String value = cellValueExtractor.extract(item, field);
                                return value != null && !value.isEmpty()
                                        ? Double.parseDouble(value.replaceAll(",", ""))
                                        : 0.0;
                            } catch (final NumberFormatException e) {
                                return 0.0;
                            }
                        })
                        .sum();

                final var cell = subtotalRow.createCell(col++);

                // 천단위 표기 적용
                final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
                final String formattedSum = numberFormat.format(sum);
                cell.setCellValue(formattedSum);

                // 왼쪽 정렬 스타일 적용
                final CellStyle leftAlignStyle = sheet.getWorkbook().createCellStyle();
                leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellStyle(leftAlignStyle);
            }
            // 지정되지 않은 필드는 소계에서 제외 (빈 셀 생성하지 않음)
        }

        // 소계 텍스트가 차지할 컬럼 수만큼 병합
        if (subtotalMergeColumns > 1) {
            final CellRangeAddress mergedRegion = new CellRangeAddress(rowIdx, rowIdx, 0, subtotalMergeColumns - 1);
            sheet.addMergedRegion(mergedRegion);
        }
    }

}