package GUI.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Lớp DatePicker đơn giản thay thế cho JDateChooser
 */
public class DatePicker extends JPanel {
    private JTextField dateTextField;
    private JButton selectButton;
    private Date selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public DatePicker() {
        this(new Date());
    }

    public DatePicker(Date initialDate) {
        setLayout(new BorderLayout(5, 0));
        this.selectedDate = initialDate;

        // Tạo text field hiển thị ngày
        dateTextField = new JTextField();
        dateTextField.setEditable(false);
        dateTextField.setText(dateFormat.format(selectedDate));

        // Tạo nút chọn ngày
        selectButton = new JButton("...");
        selectButton.setPreferredSize(new Dimension(30, 30));
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDatePickerDialog();
            }
        });

        add(dateTextField, BorderLayout.CENTER);
        add(selectButton, BorderLayout.EAST);
    }

    private void showDatePickerDialog() {
        // Tạo dialog chọn ngày
        JDialog dialog = new JDialog();
        dialog.setTitle("Chọn ngày");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        // Tạo calendar panel
        JPanel calendarPanel = createCalendarPanel(dialog);
        dialog.add(calendarPanel, BorderLayout.CENTER);

        // Hiển thị dialog
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createCalendarPanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout());

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Panel điều khiển
        JPanel controlPanel = new JPanel(new BorderLayout());

        // Combobox chọn tháng
        String[] months = { "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12" };
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(currentMonth);

        // Spinner chọn năm
        SpinnerNumberModel yearModel = new SpinnerNumberModel(currentYear, 1900, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);

        // Panel chọn tháng và năm
        JPanel monthYearPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        monthYearPanel.add(monthComboBox);
        monthYearPanel.add(yearSpinner);

        controlPanel.add(monthYearPanel, BorderLayout.CENTER);

        // Panel lịch
        JPanel calendarGrid = new JPanel(new GridLayout(7, 7));

        // Thêm tên các ngày trong tuần
        String[] dayNames = { "CN", "T2", "T3", "T4", "T5", "T6", "T7" };
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, JLabel.CENTER);
            calendarGrid.add(label);
        }

        // Thêm các ngày trong tháng
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Thêm các ô trống trước ngày đầu tiên của tháng
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.add(new JLabel(""));
        }

        // Thêm các ngày trong tháng
        for (int day = 1; day <= daysInMonth; day++) {
            final int selectedDay = day;
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Cập nhật ngày đã chọn
                    Calendar selected = Calendar.getInstance();
                    selected.set(currentYear, currentMonth, selectedDay);
                    setDate(selected.getTime());
                    dialog.dispose();
                }
            });
            calendarGrid.add(dayButton);
        }

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(calendarGrid, BorderLayout.CENTER);

        // Thêm button chọn ngày hôm nay
        JButton todayButton = new JButton("Hôm nay");
        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDate(new Date());
                dialog.dispose();
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(todayButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void setDate(Date date) {
        this.selectedDate = date;
        dateTextField.setText(dateFormat.format(date));
    }

    public Date getDate() {
        return selectedDate;
    }

    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);
        dateTextField.setPreferredSize(new Dimension(dimension.width - 35, dimension.height));
    }

    public void setFont(Font font) {
        if (dateTextField != null) {
            dateTextField.setFont(font);
        }
    }
}