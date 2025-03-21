package view;

import controller.EmployeeController;
import controller.PositionController;
import controller.SupplierController;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel mainContentPanel;

    private Employee currentUser;
    private EmployeeController employeeController;
    private PositionController positionController;
    private SupplierController supplierController;

    private JPanel homePanel;
    private EmployeeManagementPanel employeePanel;
    private PositionManagementPanel positionPanel;
    private SupplierManagementPanel supplierPanel;

    // Updated colors for a more modern look
    private Color primaryColor = new Color(0, 123, 255); // Primary blue
    private Color dangerColor = new Color(220, 53, 69); // Danger red
    private Color backgroundColor = new Color(248, 249, 250); // Light gray
    private Color textColor = Color.BLACK;
    private Color darkTextColor = new Color(33, 37, 41); // Dark text
    private Color menuHoverColor = new Color(0, 105, 217);

    public MainView(Employee currentUser) {
        this.currentUser = currentUser;
        this.employeeController = new EmployeeController();
        this.positionController = new PositionController();
        this.supplierController = new SupplierController();

        initializeUI();
        showHomePanel();
    }

    private void initializeUI() {
        setTitle("Hệ Thống Quản Lý Nước Giải Khát");
        setSize(1280, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);

        setupMenuPanel();
        setupMainContentPanel();

        contentPanel.add(menuPanel, BorderLayout.WEST);
        contentPanel.add(mainContentPanel, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    private void setupMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(primaryColor);
        menuPanel.setPreferredSize(new Dimension(240, getHeight()));

        // User info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(new Color(0, 100, 200));
        userInfoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // User image icon
        JLabel userIconLabel = new JLabel("\u263A"); // Ký tự unicode mặt cười
        userIconLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        userIconLabel.setForeground(textColor);
        userIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        nameLabel.setForeground(textColor);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String roleName = "";
        if (currentUser.getPositionId().equals("CV001")) {
            roleName = "Quản trị viên";
        } else if (currentUser.getPositionId().equals("CV002")) {
            roleName = "Quản lý";
        } else {
            roleName = "Nhân viên bán hàng";
        }

        JLabel roleLabel = new JLabel("Vai trò: " + roleName);
        roleLabel.setForeground(textColor);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutButton = new JButton("\u21AA Đăng xuất"); // Ký tự unicode mũi tên
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 14));
        logoutButton.setBackground(dangerColor);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setMaximumSize(new Dimension(150, 35));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> logout());

        userInfoPanel.add(userIconLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(roleLabel);
        userInfoPanel.add(Box.createVerticalStrut(15));
        userInfoPanel.add(logoutButton);

        // Menu navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(primaryColor);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Trang chủ button
        JButton homeButton = createNavigationButton("Trang chủ", "\u2302"); // Ký tự unicode nhà
        homeButton.addActionListener(e -> showHomePanel());

        // Divider
        JSeparator divider1 = new JSeparator();
        divider1.setMaximumSize(new Dimension(240, 1));
        divider1.setForeground(new Color(255, 255, 255, 100));
        divider1.setBackground(primaryColor);

        // Quản lý dropdown menu
        JPanel managementPanel = createDropdownMenu("Quản lý", "\u2630"); // Ký tự unicode menu

        // Các nút menu con của Quản lý
        JButton employeeButton = createSubMenuButton("Quản lý nhân viên", "\u263A");
        employeeButton.addActionListener(e -> showEmployeePanel());

        JButton positionButton = createSubMenuButton("Quản lý chức vụ", "\u2691");
        positionButton.addActionListener(e -> showPositionPanel());

        JButton supplierButton = createSubMenuButton("Quản lý nhà cung cấp", "\u25A3");
        supplierButton.addActionListener(e -> showSupplierPanel());

        // Thêm các nút menu con vào panel
        JPanel managementSubMenu = (JPanel) managementPanel.getComponent(1);
        managementSubMenu.add(employeeButton);
        managementSubMenu.add(positionButton);
        if (!currentUser.getPositionId().equals("CV003")) { // Not a regular employee
            managementSubMenu.add(supplierButton);
        }
        managementSubMenu.setVisible(false); // Mặc định ẩn submenu

        // Thống kê dropdown menu
        JPanel statisticsPanel = createDropdownMenu("Thống kê", "\u2315"); // Ký tự unicode biểu đồ

        // Thêm các nút menu con vào panel
        JPanel statisticsSubMenu = (JPanel) statisticsPanel.getComponent(1);
        statisticsSubMenu.setVisible(false); // Mặc định ẩn submenu

        // Add everything to navigation panel
        navigationPanel.add(homeButton);
        navigationPanel.add(Box.createVerticalStrut(15));
        navigationPanel.add(divider1);
        navigationPanel.add(Box.createVerticalStrut(10));
        // Đặt Quản lý và Thống kê gần nhau
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(primaryColor);
        menuContainer.add(managementPanel);
        menuContainer.add(statisticsPanel);

        navigationPanel.add(menuContainer);

        // Version info at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(primaryColor);
        JLabel versionLabel = new JLabel("Phiên bản 1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(Color.BLACK);
        bottomPanel.add(versionLabel);

        // Add main sections to menu panel
        menuPanel.add(userInfoPanel, BorderLayout.NORTH);
        menuPanel.add(navigationPanel, BorderLayout.CENTER);
        menuPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createDropdownMenu(String title, String icon) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(primaryColor);
        menuPanel.setMaximumSize(new Dimension(240, 1000)); // Cho phép mở rộng khi dropdown

        // Nút tiêu đề dropdown
        JButton titleButton = new JButton(icon + " " + title + " \u25BC"); // Unicode mũi tên xuống
        titleButton.setFont(new Font("Dialog", Font.BOLD, 14));
        titleButton.setForeground(Color.WHITE);
        titleButton.setBackground(primaryColor);
        titleButton.setBorderPainted(false);
        titleButton.setFocusPainted(false);
        titleButton.setMaximumSize(new Dimension(240, 36));
        titleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleButton.setHorizontalAlignment(SwingConstants.LEFT);
        titleButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        titleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Submenu panel - với màu nền tối và chữ màu sáng
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setBackground(new Color(0, 80, 180)); // Màu nền tối hơn
        subMenuPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        subMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Toggle action cho nút tiêu đề
        titleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isVisible = subMenuPanel.isVisible();
                subMenuPanel.setVisible(!isVisible);
                titleButton.setText(icon + " " + title + (isVisible ? " \u25BC" : " \u25C0")); // Unicode mũi tên
                                                                                               // xuống/trái

                // Thêm khoảng cách khi dropdown
                if (!isVisible) {
                    // Nếu đang mở submenu, thêm khoảng cách
                    subMenuPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                } else {
                    // Nếu đang đóng submenu, bỏ khoảng cách
                    subMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }

                menuPanel.revalidate();
                menuPanel.repaint();
            }
        });

        // Hover effect
        titleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                titleButton.setBackground(menuHoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                titleButton.setBackground(primaryColor);
            }
        });

        menuPanel.add(titleButton);
        menuPanel.add(subMenuPanel);

        return menuPanel;
    }

    private JButton createNavigationButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(240, 36));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(menuHoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private JButton createSubMenuButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 80, 180));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(240, 30));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 60, 160)); // Màu hover tối hơn cho submenu
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 80, 180));
            }
        });

        return button;
    }

    private void setupMainContentPanel() {
        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(backgroundColor);

        // Initialize panels
        homePanel = createHomePanel();
        employeePanel = new EmployeeManagementPanel(employeeController, positionController);
        positionPanel = new PositionManagementPanel(positionController);
        supplierPanel = new SupplierManagementPanel(supplierController);

        // Add panels to the main content
        mainContentPanel.add(homePanel, "home");
        mainContentPanel.add(employeePanel, "employee");
        mainContentPanel.add(positionPanel, "position");
        mainContentPanel.add(supplierPanel, "supplier");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        // Welcome header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255, 20));
        headerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel welcomeLabel = new JLabel("Chào mừng đến với Hệ Thống Quản Lý Nước Giải Khát");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(darkTextColor);

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setOpaque(false);
        welcomeTextPanel.add(welcomeLabel);
        welcomeTextPanel.add(Box.createVerticalStrut(5));

        headerPanel.add(welcomeTextPanel, BorderLayout.CENTER);

        // Content panel - để trống theo yêu cầu
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(backgroundColor);

        // Layout setup
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showHomePanel() {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "home");
    }

    private void showEmployeePanel() {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "employee");
    }

    private void showPositionPanel() {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "position");
    }

    private void showSupplierPanel() {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "supplier");
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}