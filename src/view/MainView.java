package view;

import controller.*;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainView extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel mainContentPanel;

    private Employee currentUser;
    private EmployeeController employeeController;
    private PositionController positionController;
    private SupplierController supplierController;
    private ProductCategoryController categoryController;
    private ProductController productController;
    private ProductDetailController productDetailController;

    private JPanel homePanel;
    private EmployeeManagementPanel employeePanel;
    private PositionManagementPanel positionPanel;
    private SupplierManagementPanel supplierPanel;
    private ProductCategoryManagementPanel categoryPanel;
    private ProductManagementPanel productPanel;
    private ProductDetailManagementPanel productDetailPanel;
    private ProductStatisticsPanel productStatisticsPanel;

    // Updated colors for a more modern look
    private Color primaryColor = new Color(51, 122, 183); // Primary blue like in TCSOFT HOTEL
    private Color dangerColor = new Color(217, 83, 79);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = Color.BLACK;
    private Color menuHoverColor = new Color(41, 112, 173);
    private Color headerColor = new Color(51, 122, 183); // Header blue color

    public MainView(Employee currentUser) {
        this.currentUser = currentUser;
        this.employeeController = new EmployeeController();
        this.positionController = new PositionController();
        this.supplierController = new SupplierController();
        this.categoryController = new ProductCategoryController();
        this.productController = new ProductController();
        this.productDetailController = new ProductDetailController();

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

        // Tạo logo ứng dụng ở bên trái
        JLabel logoLabel = new JLabel("BEVERAGE MANAGEMENT");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // Tạo menu tabs ở giữa header
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        tabsPanel.setOpaque(false);

        String[] mainTabs = { "QUẢN LÝ", "KHÁCH HÀNG", "NHÀ CUNG CẤP", "BÁO CÁO" };
        for (String tabName : mainTabs) {
            JButton tabButton = new JButton(tabName);
            tabButton.setFont(new Font("Arial", Font.BOLD, 14));
            tabButton.setForeground(Color.WHITE);
            tabButton.setBackground(headerColor);
            tabButton.setBorderPainted(false);
            tabButton.setFocusPainted(false);
            tabButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Thêm hover effect
            tabButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    tabButton.setBackground(menuHoverColor);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    tabButton.setBackground(headerColor);
                }
            });

            tabsPanel.add(tabButton);
        }
        headerPanel.add(tabsPanel, BorderLayout.CENTER);

        // Thêm icons ở bên phải header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        rightPanel.setOpaque(false);

        JLabel notificationIcon = new JLabel("\uD83D\uDD14"); // Unicode bell icon
        notificationIcon.setFont(new Font("Arial", Font.PLAIN, 18));
        notificationIcon.setForeground(Color.WHITE);
        notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel userIcon = new JLabel("\uD83D\uDC64"); // Unicode user icon
        userIcon.setFont(new Font("Arial", Font.PLAIN, 18));
        userIcon.setForeground(Color.WHITE);
        userIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightPanel.add(notificationIcon);
        rightPanel.add(userIcon);
        rightPanel.add(Box.createHorizontalStrut(20)); // Spacing

        headerPanel.add(rightPanel, BorderLayout.EAST);

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

        // Navigation panel with compact layout
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(primaryColor);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Home button
        JButton homeButton = createNavigationButton("Trang chủ", "\uD83C\uDFE0");
        homeButton.addActionListener(e -> showHomePanel());

        // Divider
        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(255, 255, 255, 100));
        divider.setBackground(primaryColor);
        divider.setMaximumSize(new Dimension(220, 1));

        // Create dropdown menus
        // 1. Main Management Menu
        JPanel managementPanel = createDropdownMenu("Quản lý", "\uD83D\uDCCA");
        JPanel managementSubMenu = (JPanel) managementPanel.getComponent(1);

        // 2. Statistics Menu (new)
        JPanel statisticsPanel = createDropdownMenu("Thống kê", "\uD83D\uDCC8");
        JPanel statisticsSubMenu = (JPanel) statisticsPanel.getComponent(1);

        // All management submenu items
        // Employee management
        JButton employeeButton = createSubMenuButton("Quản lý nhân viên", "\uD83D\uDC64");
        employeeButton.addActionListener(e -> showEmployeePanel());

        // Position management
        JButton positionButton = createSubMenuButton("Quản lý chức vụ", "\uD83D\uDCBC");
        positionButton.addActionListener(e -> showPositionPanel());

        // Supplier management
        JButton supplierButton = createSubMenuButton("Quản lý nhà cung cấp", "\uD83D\uDEE0");
        supplierButton.addActionListener(e -> showSupplierPanel());

        // Product category management
        JButton categoryButton = createSubMenuButton("Quản lý loại sản phẩm", "\uD83D\uDCC1");
        categoryButton.addActionListener(e -> showCategoryPanel());

        // Product management
        JButton productButton = createSubMenuButton("Quản lý sản phẩm", "\uD83C\uDF7A");
        productButton.addActionListener(e -> showProductPanel());

        // Product detail management
        JButton productDetailButton = createSubMenuButton("Quản lý chi tiết SP", "\uD83D\uDDD3");
        productDetailButton.addActionListener(e -> showProductDetailPanel());

        // Statistics submenu items
        JButton statisticsButton = createSubMenuButton("Thống kê sản phẩm", "\uD83D\uDCCA");
        statisticsButton.addActionListener(e -> showProductStatisticsPanel());

        // Add all management items to the management submenu
        managementSubMenu.add(employeeButton);
        managementSubMenu.add(positionButton);
        managementSubMenu.add(supplierButton);
        managementSubMenu.add(categoryButton);
        managementSubMenu.add(productButton);
        managementSubMenu.add(productDetailButton);

        // Add statistics items to the statistics submenu
        statisticsSubMenu.add(statisticsButton);

        // Add everything to the navigation panel in order
        navigationPanel.add(homeButton);
        navigationPanel.add(divider);
        navigationPanel.add(managementPanel);
        navigationPanel.add(statisticsPanel);

        // Add the user info and navigation panels to the main menu panel
        menuPanel.add(userInfoPanel, BorderLayout.NORTH);
        menuPanel.add(navigationPanel, BorderLayout.CENTER);
    }

    private JPanel createDropdownMenu(String title, String icon) {
        // Main container that holds everything
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(primaryColor);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainContainer.setMaximumSize(new Dimension(220, 1000));

        // Title button
        JButton titleButton = new JButton(icon + " " + title);
        titleButton.setFont(new Font("Arial", Font.BOLD, 16));
        titleButton.setHorizontalAlignment(SwingConstants.LEFT);
        titleButton.setBackground(primaryColor);
        titleButton.setForeground(Color.WHITE);
        titleButton.setFocusPainted(false);
        titleButton.setBorderPainted(false);
        titleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Fixed dimensions for title button
        titleButton.setPreferredSize(new Dimension(220, 36));
        titleButton.setMaximumSize(new Dimension(220, 36));
        titleButton.setMinimumSize(new Dimension(220, 36));

        // Sub-menu panel that will be shown/hidden
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setBackground(new Color(41, 112, 173));
        subMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        subMenuPanel.setVisible(false);

        // Toggle behavior for dropdown
        titleButton.addActionListener(e -> {
            subMenuPanel.setVisible(!subMenuPanel.isVisible());
            mainContainer.revalidate();
            mainContainer.repaint();
        });

        // Hover effect
        titleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                titleButton.setBackground(menuHoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                titleButton.setBackground(primaryColor);
            }
        });

        // Add components to main container
        mainContainer.add(titleButton);
        mainContainer.add(subMenuPanel);

        return mainContainer;
    }

    private JButton createNavigationButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setMaximumSize(new Dimension(220, 36));

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

    private JButton createSubMenuButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setBackground(new Color(41, 112, 173));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 15));

        // Fixed dimensions
        Dimension buttonSize = new Dimension(220, 32);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);

        // Proper text display
        button.setIconTextGap(8);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(31, 102, 163));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 112, 173));
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
            employeePanel = new EmployeeManagementPanel(employeeController, positionController);
        }
        showPanel(employeePanel);
    }

    private void showPositionPanel() {
        if (positionPanel == null) {
            positionPanel = new PositionManagementPanel(positionController);
        }
        showPanel(positionPanel);
    }

    private void showSupplierPanel() {
        if (supplierPanel == null) {
            supplierPanel = new SupplierManagementPanel(supplierController);
        }
        showPanel(supplierPanel);
    }

    private void showCategoryPanel() {
        if (categoryPanel == null) {
            categoryPanel = new ProductCategoryManagementPanel(categoryController);
        }
        showPanel(categoryPanel);
    }

    private void showProductPanel() {
        if (productPanel == null) {
            productPanel = new ProductManagementPanel(productController, categoryController);
        }
        showPanel(productPanel);
    }

    private void showProductDetailPanel() {
        if (productDetailPanel == null) {
            productDetailPanel = new ProductDetailManagementPanel(productDetailController, productController,
                    supplierController);
        }
        showPanel(productDetailPanel);
    }

    private void showProductStatisticsPanel() {
        if (productStatisticsPanel == null) {
            productStatisticsPanel = new ProductStatisticsPanel(productController);
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
        int result = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}