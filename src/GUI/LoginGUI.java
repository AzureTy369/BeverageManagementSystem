package GUI;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    // Colors
    private Color primaryColor = new Color(0, 120, 212);
    private Color lightColor = new Color(248, 249, 250);
    private Color accentColor = new Color(16, 163, 127);
    private Color textColor = Color.BLACK;

    private EmployeeBUS employeeController;

    public LoginGUI() {
        employeeController = new EmployeeBUS();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng Nhập - Beverage Management System");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(lightColor);

        // Header với tên ứng dụng
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(450, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblAppName = new JLabel("BEVERAGE MANAGEMENT SYSTEM");
        lblAppName.setFont(new Font("Arial", Font.BOLD, 18));
        lblAppName.setForeground(Color.BLACK);
        headerPanel.add(lblAppName, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(lightColor);
        loginPanel.setBorder(new EmptyBorder(40, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Đăng Nhập Hệ Thống");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(lblTitle, gbc);

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 8, 5);
        loginPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 5, 8, 5);
        loginPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        loginPanel.add(txtPassword, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(accentColor);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(evt -> loginAction());
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(accentColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(accentColor);
            }
        });

        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Arial", Font.BOLD, 14));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(evt -> System.exit(0));
        btnExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnExit.setBackground(new Color(200, 35, 51));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnExit.setBackground(new Color(220, 53, 69));
            }
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 5, 8, 5);
        loginPanel.add(buttonPanel, gbc);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        getContentPane().add(mainPanel);

        // Press Enter to login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void loginAction() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu", "Lỗi đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EmployeeDTO employee = employeeController.login(username, password);

        if (employee != null) {
            openMainView(employee);
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng", "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void openMainView(EmployeeDTO employee) {
        this.dispose();
        SwingUtilities.invokeLater(() -> new MainGUI(employee).setVisible(true));
    }
}