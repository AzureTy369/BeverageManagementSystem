package GUI;

import BUS.*;
import DTO.EmployeeDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainGUI extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel mainContentPanel;

    private EmployeeDTO currentUser;
    private EmployeeBUS employeeController;
    private PositionBUS positionController;
    private SupplierBUS supplierController;
    private ProductCategoryBUS categoryController;
    private ProductBUS productController;
    private ProductDetailBUS productDetailController;

    private JPanel homePanel;
    private EmployeeGUI employeePanel;
    private PositionGUI positionPanel;
    private SupplierGUI supplierPanel;
    private ProductManagementGUI productPanel;
    private ProductStatisticsGUI productStatisticsPanel;

    // Các màu sắc chung cho giao diện
    private final Color primaryColor = new Color(51, 122, 183);
    private final Color dangerColor = new Color(217, 83, 79);
    private final Color backgroundColor = new Color(248, 249, 250);
    private final Color textColor = Color.BLACK;
    private final Color menuHoverColor = new Color(41, 112, 173);
    private final Color headerColor = new Color(51, 122, 183);

    public MainGUI(EmployeeDTO currentUser) {
        this.currentUser = currentUser;
        this.employeeController = new EmployeeBUS();
        this.positionController = new PositionBUS();
        this.supplierController = new SupplierBUS();
        this.categoryController = new ProductCategoryBUS();
        this.productController = new ProductBUS();
        this.productDetailController = new ProductDetailBUS();

        initializeUI();
        showHomePanel();
    }

    private void initializeUI() {
        setTitle("Hệ Thống Quản Lý Nước Giải Khát");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);

        setupMenuPanel();
        setupMainContentPanel();

        // Add header at NORTH, menu at WEST, and content at CENTER
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(menuPanel, BorderLayout.WEST);
        contentPanel.add(mainContentPanel, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        // Tạo logo ứng dụng ở giữa
        JLabel logoLabel = new JLabel("QUẢN LÝ BÁN NƯỚC GIẢI KHÁT");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(logoLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private void setupMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(primaryColor);
        menuPanel.setPreferredSize(new Dimension(220, getHeight()));

        // User info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(new Color(41, 112, 173));
        userInfoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // User image icon
        JLabel userIconLabel = new JLabel("\uD83D\uDC64"); // Unicode user icon
        userIconLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        userIconLabel.setForeground(Color.WHITE);
        userIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        nameLabel.setForeground(Color.WHITE);
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
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 14));
        logoutButton.setBackground(dangerColor);
        logoutButton.setForeground(Color.WHITE);
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

        // Navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(primaryColor);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Home button
        JButton homeButton = createNavigationButton("Trang chủ", "\uD83C\uDFE0");
        homeButton.addActionListener(e -> showHomePanel());

        // Divider
        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(255, 255, 255, 100));
        divider.setBackground(primaryColor);
        divider.setMaximumSize(new Dimension(220, 1));

        // Add menu items
        navigationPanel.add(homeButton);
        navigationPanel.add(divider);
        navigationPanel.add(Box.createVerticalStrut(10));

        // Quản lý nhân viên
        JButton employeeButton = createNavigationButton("Nhân viên", "\uD83D\uDC64");
        employeeButton.addActionListener(e -> showEmployeePanel());
        navigationPanel.add(employeeButton);

        // Quản lý chức vụ
        JButton positionButton = createNavigationButton("Chức vụ", "\uD83D\uDCBC");
        positionButton.addActionListener(e -> showPositionPanel());
        navigationPanel.add(positionButton);

        // Quản lý nhà cung cấp
        JButton supplierButton = createNavigationButton("Nhà cung cấp", "\uD83D\uDEE0");
        supplierButton.addActionListener(e -> showSupplierPanel());
        navigationPanel.add(supplierButton);

        // Quản lý sản phẩm
        JButton productButton = createNavigationButton("Sản phẩm", "\uD83C\uDF7A");
        productButton.addActionListener(e -> showProductPanel());
        navigationPanel.add(productButton);

        // Thống kê sản phẩm
        JButton statisticsButton = createNavigationButton("Thống kê sản phẩm", "\uD83D\uDCCA");
        statisticsButton.addActionListener(e -> showProductStatisticsPanel());
        navigationPanel.add(statisticsButton);

        // Add scrolling capability
        JScrollPane scrollPane = new JScrollPane(navigationPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add the user info and navigation panels to the main menu panel
        menuPanel.add(userInfoPanel, BorderLayout.NORTH);
        menuPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createNavigationButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setMaximumSize(new Dimension(220, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // All navigation buttons should be left aligned
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);

        // Add spacing between icon and text
        button.setIconTextGap(8);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(menuHoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private void setupMainContentPanel() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(backgroundColor);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Initialize content panels
        homePanel = createHomePanel();
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JLabel welcomeLabel = new JLabel("Chào mừng đến với Hệ thống Quản lý Nước giải khát!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descriptionLabel = new JLabel(
                "<html><div style='text-align: center;'>Sử dụng menu bên trái để truy cập các chức năng của hệ thống.</div></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(textColor);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        textPanel.setBackground(backgroundColor);
        textPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        textPanel.add(welcomeLabel);
        textPanel.add(descriptionLabel);

        contentPanel.add(textPanel, gbc);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showHomePanel() {
        showPanel(homePanel);
    }

    private void showEmployeePanel() {
        if (employeePanel == null) {
            employeePanel = new EmployeeGUI(employeeController, positionController);
        }
        showPanel(employeePanel);
    }

    private void showPositionPanel() {
        if (positionPanel == null) {
            positionPanel = new PositionGUI(positionController);
        }
        showPanel(positionPanel);
    }

    private void showSupplierPanel() {
        if (supplierPanel == null) {
            supplierPanel = new SupplierGUI(supplierController);
        }
        showPanel(supplierPanel);
    }

    private void showProductPanel() {
        if (productPanel == null) {
            productPanel = new ProductManagementGUI(productController, productDetailController, categoryController,
                    supplierController);
        }
        showPanel(productPanel);
    }

    private void showProductStatisticsPanel() {
        if (productStatisticsPanel == null) {
            productStatisticsPanel = new ProductStatisticsGUI(productController, productDetailController);
        }
        showPanel(productStatisticsPanel);
    }

    // Helper method to show panels
    private void showPanel(JPanel panel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}