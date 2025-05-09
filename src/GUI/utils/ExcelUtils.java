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
     * @param headers   Tiêu đề các cột
     * @param data      Dữ liệu cần xuất
     * @param sheetName Tên sheet
     * @param title     Tiêu đề bảng
     * @return true nếu xuất thành công, false nếu thất bại
     */
    public static boolean exportToExcel(String[] headers, List<Object[]> data, String sheetName, String title) {
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

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            // Tạo workbook mới
            XSSFWorkbook workbook = new XSSFWorkbook();
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
                        cell.setCellValue(rowData[i].toString());
                    }
                }
            }

            // Lưu file
            workbook.write(fileOut);
            workbook.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Nhập dữ liệu từ file Excel
     * 
     * @param headers Tiêu đề các cột
     * @return Danh sách dữ liệu đã nhập
     */
    public static List<Object[]> importFromExcel(String[] headers) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = fileChooser.getSelectedFile();
        List<Object[]> data = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        StringBuilder duplicateMessage = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // Kiểm tra header
            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                throw new IOException("Không tìm thấy hàng tiêu đề trong file Excel");
            }

            // Tìm cột mã sản phẩm
            int maSanPhamIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].toLowerCase().contains("mã sản phẩm") ||
                        headers[i].toLowerCase().contains("mã sp")) {
                    maSanPhamIndex = i;
                    break;
                }
            }

            // Bỏ qua 2 dòng đầu (tiêu đề và header)
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                Object[] rowData = new Object[headers.length];
                boolean isValidRow = true;

                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = cell != null ? formatter.formatCellValue(cell).trim() : "";

                    // Kiểm tra giá trị không được trống cho các trường bắt buộc
                    if (cellValue.isEmpty() && !headers[j].toLowerCase().contains("ghi chú")) {
                        isValidRow = false;
                        errorMessage.append("Dòng ").append(i + 1).append(": Thiếu dữ liệu ở cột ")
                                .append(headers[j]).append("\n");
                        continue;
                    }

                    // Xử lý dữ liệu theo loại cột
                    if (headers[j].toLowerCase().contains("mã")) {
                        // Đảm bảo mã không chứa khoảng trắng
                        rowData[j] = cellValue.replaceAll("\\s+", "");
                    } else if (headers[j].toLowerCase().contains("giá") ||
                            headers[j].toLowerCase().contains("tiền")) {
                        try {
                            // Chuyển đổi giá trị thành số
                            rowData[j] = Double.parseDouble(cellValue.replaceAll("[^\\d.]", ""));
                        } catch (NumberFormatException e) {
                            isValidRow = false;
                            errorMessage.append("Dòng ").append(i + 1).append(": Giá trị không hợp lệ ở cột ")
                                    .append(headers[j]).append("\n");
                        }
                    } else {
                        rowData[j] = cellValue;
                    }
                }

                if (isValidRow) {
                    // Kiểm tra xem mã sản phẩm có trùng lặp không
                    if (maSanPhamIndex >= 0) {
                        String maSanPham = (String) rowData[maSanPhamIndex];
                        boolean isDuplicate = false;

                        // Kiểm tra trong danh sách đã nhập
                        for (Object[] existingRow : data) {
                            if (maSanPham.equals(existingRow[maSanPhamIndex])) {
                                isDuplicate = true;
                                duplicateMessage.append("Dòng ").append(i + 1)
                                        .append(": Mã sản phẩm '").append(maSanPham)
                                        .append("' đã tồn tại trong file Excel\n");
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            data.add(rowData);
                        }
                    } else {
                        data.add(rowData);
                    }
                }
            }

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Không có dữ liệu hợp lệ nào được nhập!\n" + errorMessage.toString(),
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            // Hiển thị thông báo kết quả
            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("Đã nhập ").append(data.size()).append(" dòng dữ liệu thành công.");

            if (duplicateMessage.length() > 0) {
                resultMessage.append("\n\nCác mã sản phẩm trùng lặp (đã bỏ qua):\n").append(duplicateMessage);
            }

            if (errorMessage.length() > 0) {
                resultMessage.append("\n\nCác lỗi khác gặp phải:\n").append(errorMessage);
            }

            JOptionPane.showMessageDialog(null,
                    resultMessage.toString(),
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi nhập dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
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