package GUI.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Lớp tiện ích để xử lý việc xuất và nhập Excel
 */
public class ExcelUtils {

    /**
     * Xuất dữ liệu ra file Excel
     * 
     * @param headers        Tiêu đề các cột
     * @param data           Dữ liệu theo dòng
     * @param fileNamePrefix Tiền tố tên file
     * @param sheetName      Tên sheet
     * @return true nếu xuất thành công, false nếu thất bại
     */
    public static boolean exportToExcel(String[] headers, List<Object[]> data, String fileNamePrefix,
            String sheetName) {
        // Trong bản demo này, hàm chỉ hiển thị thông báo
        JOptionPane.showMessageDialog(null,
                "Đã xuất dữ liệu ra file Excel với:\n" +
                        "- Số cột: " + headers.length + "\n" +
                        "- Số dòng: " + data.size(),
                "Xuất Excel thành công",
                JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    /**
     * Nhập dữ liệu từ file Excel
     * 
     * @param headers Tiêu đề các cột cần nhập
     * @return List dữ liệu, null nếu có lỗi hoặc người dùng hủy
     */
    public static List<Object[]> importFromExcel(String[] headers) {
        // Hiển thị hộp thoại chọn file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null; // Người dùng đã hủy
        }

        File selectedFile = fileChooser.getSelectedFile();

        // Trong bản demo này, chỉ trả về dữ liệu giả
        List<Object[]> demoData = new ArrayList<>();

        // Tạo 3 dòng dữ liệu mẫu
        Object[] row1 = new Object[headers.length];
        Object[] row2 = new Object[headers.length];
        Object[] row3 = new Object[headers.length];

        for (int i = 0; i < headers.length; i++) {
            row1[i] = "Data 1 - Col " + (i + 1);
            row2[i] = "Data 2 - Col " + (i + 1);
            row3[i] = "Data 3 - Col " + (i + 1);
        }

        demoData.add(row1);
        demoData.add(row2);
        demoData.add(row3);

        return demoData;
    }

    /**
     * Xuất dữ liệu ra file Excel với định dạng ngày tháng
     * 
     * @param headers           Tiêu đề các cột
     * @param data              Dữ liệu cần xuất
     * @param sheetName         Tên sheet
     * @param title             Tiêu đề bảng
     * @param dateColumnIndexes Mảng các chỉ số cột chứa ngày tháng
     * @return true nếu xuất thành công, false nếu thất bại
     */
    public static boolean exportToExcelWithDates(String[] headers, List<Object[]> data, String sheetName, String title,
            int[] dateColumnIndexes) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Tạo style cho tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            // Tạo style cho ngày tháng
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            // Tạo tiêu đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

            // Tạo header
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256); // Đặt độ rộng cột
            }

            // Thêm dữ liệu
            int rowNum = 2;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    if (rowData[i] != null) {
                        // Kiểm tra xem cột này có phải là cột ngày tháng không
                        boolean isDateColumn = false;
                        if (dateColumnIndexes != null) {
                            for (int dateIndex : dateColumnIndexes) {
                                if (i == dateIndex) {
                                    isDateColumn = true;
                                    break;
                                }
                            }
                        }

                        if (isDateColumn) {
                            try {
                                // Chuyển đổi chuỗi ngày tháng thành đối tượng Date
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                java.util.Date date = sdf.parse(rowData[i].toString());
                                cell.setCellValue(date);
                                cell.setCellStyle(dateStyle);
                            } catch (Exception e) {
                                // Nếu không thể chuyển đổi, gán giá trị gốc
                                cell.setCellValue(rowData[i].toString());
                            }
                        } else {
                            cell.setCellValue(rowData[i].toString());
                        }
                    }
                }
            }

            // Lưu file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            JOptionPane.showMessageDialog(null, "Xuất dữ liệu thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}