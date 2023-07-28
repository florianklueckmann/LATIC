package software.latic.helper;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import software.latic.item.TextItemData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class XlsxBuilder {
    private static final XlsxBuilder xlsxBuilder = new XlsxBuilder();
    public static XlsxBuilder getInstance() {
        return xlsxBuilder;
    }
    public Workbook createWorkbookFromTable(TableView<TextItemData> table) {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Results"); //TODO Translate

        var headers = table.getColumns().stream().map(TableColumnBase::getText).toList();

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        CellStyle dataStyle = workbook.createCellStyle();
        Font dataFont = workbook.createFont();
        dataStyle.setFont(dataFont);

        addHeaderRowToSheet(sheet, headers, headerStyle);

        addColumnsToSheet(sheet, table, dataStyle);

        return workbook;
    }

    private void addHeaderRowToSheet(Sheet sheet, List<String> headers, CellStyle style) {
        Row header = sheet.createRow(0);

        for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
            var cell = header.createCell(colIndex);
            cell.setCellStyle(style);
            cell.setCellValue(headers.get(colIndex));
            sheet.autoSizeColumn(colIndex);
        }

        //Set first column width manually because the Text column is very small after autoSizeColumn
        sheet.setColumnWidth(0, 20*255);
    }

    private void addColumnsToSheet(Sheet sheet, TableView<TextItemData> table, CellStyle style) {
        for (int rowIndex = 0; rowIndex < table.getItems().size(); rowIndex++) {
            var row = sheet.createRow(rowIndex + 1);
            for (int colIndex = 0; colIndex < table.getColumns().size(); colIndex++) {
                if (table.getColumns().get(colIndex).getCellData(rowIndex) != null) {
                    var cell = row.createCell(colIndex);
                    var content = table.getColumns().get(colIndex).getCellData(rowIndex).toString();
                    content = StringUtils.abbreviate(content, 32767);
                    cell.setCellValue(content);
                    cell.setCellStyle(style);
                } else {
                    row.createCell(colIndex).setCellValue("");
                }
            }
        }
    }

    public File writeToFile(File file, TableView<TextItemData> table) throws IOException
    {
        var workbook = createWorkbookFromTable(table);

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();

        return file;
    }
}
