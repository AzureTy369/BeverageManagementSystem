package BUS;

import DAO.EmployeeDAO;
import DTO.EmployeeDTO;
import java.util.List;
import java.util.ArrayList;

public class EmployeeBUS {
    private EmployeeDAO employeeDAO;

    public EmployeeBUS() {
        employeeDAO = new EmployeeDAO();
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public EmployeeDTO getEmployeeById(String employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }

    public EmployeeDTO getEmployeeByUsername(String username) {
        return employeeDAO.getEmployeeByUsername(username);
    }

    public boolean addEmployee(String employeeId, String username, String password,
            String firstName, String lastName, String positionId, String phone) {
        System.out
                .println("EmployeeBUS.addEmployee: Adding employee with ID: " + employeeId + ", username: " + username);
        System.out.println("First name (tên): " + firstName); // firstName = tên
        System.out.println("Last name (họ): " + lastName); // lastName = họ

        try {
            // Kiểm tra thông tin nhập vào
            if (employeeId == null || employeeId.trim().isEmpty()) {
                System.out.println("Mã nhân viên trống, không thể thêm nhân viên");
                return false;
            }
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Tên đăng nhập trống, không thể thêm nhân viên");
                return false;
            }
            if (password == null || password.trim().isEmpty()) {
                System.out.println("Mật khẩu trống, không thể thêm nhân viên");
                return false;
            }
            if (firstName == null || firstName.trim().isEmpty()) {
                System.out.println("Tên trống, không thể thêm nhân viên");
                return false;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                System.out.println("Họ trống, không thể thêm nhân viên");
                return false;
            }
            if (positionId == null || positionId.trim().isEmpty()) {
                System.out.println("Mã chức vụ trống, không thể thêm nhân viên");
                return false;
            }
            if (phone == null || phone.trim().isEmpty()) {
                System.out.println("Số điện thoại trống, không thể thêm nhân viên");
                return false;
            }

            // Check if username already exists
            boolean usernameExists = employeeDAO.isUsernameExists(username);
            System.out.println("Username exists check result: " + usernameExists);

            if (usernameExists) {
                System.out.println("Username already exists, cannot add employee: " + username);
                return false;
            }

            EmployeeDTO employee = new EmployeeDTO(employeeId, username, password, firstName, lastName, positionId,
                    phone);

            // Log the order of parameters again to double check
            System.out.println("Creating EmployeeDTO with the following details:");
            System.out.println("  employeeId: " + employeeId);
            System.out.println("  username: " + username);
            System.out.println("  password: " + password);
            System.out.println("  firstName (tên): " + firstName);
            System.out.println("  lastName (họ): " + lastName);
            System.out.println("  positionId: " + positionId);
            System.out.println("  phone: " + phone);

            boolean result = employeeDAO.addEmployee(employee);
            System.out.println("Result of adding employee to database: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Exception in addEmployee method: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addEmployee(EmployeeDTO employee) {
        System.out.println("EmployeeBUS.addEmployee: Adding employee with object: " +
                employee.getEmployeeId() + ", username: " + employee.getUsername());

        try {
            // Kiểm tra thông tin nhập vào
            if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty() ||
                    employee.getUsername() == null || employee.getUsername().trim().isEmpty() ||
                    employee.getPassword() == null || employee.getPassword().trim().isEmpty() ||
                    employee.getFirstName() == null || employee.getFirstName().trim().isEmpty() ||
                    employee.getLastName() == null || employee.getLastName().trim().isEmpty() ||
                    employee.getPositionId() == null || employee.getPositionId().trim().isEmpty() ||
                    employee.getPhone() == null || employee.getPhone().trim().isEmpty()) {
                System.out.println("Some employee information is empty, cannot add employee");
                return false;
            }

            // Check if username already exists
            boolean usernameExists = employeeDAO.isUsernameExists(employee.getUsername());
            System.out.println("Username exists check result: " + usernameExists);

            if (usernameExists) {
                System.out.println("Username already exists, cannot add employee: " + employee.getUsername());
                return false;
            }

            boolean result = employeeDAO.addEmployee(employee);
            System.out.println("Result of adding employee to database: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Exception in addEmployee object method: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(String employeeId, String username, String password,
            String firstName, String lastName, String positionId, String phone) {
        // Get current employee data
        EmployeeDTO currentEmployee = employeeDAO.getEmployeeById(employeeId);

        // Check if the employee exists
        if (currentEmployee == null) {
            return false;
        }

        // Check if username is being changed and if the new username already exists
        if (!currentEmployee.getUsername().equals(username) && employeeDAO.isUsernameExists(username)) {
            return false;
        }

        EmployeeDTO employee = new EmployeeDTO(employeeId, username, password, firstName, lastName, positionId, phone);
        return employeeDAO.updateEmployee(employee);
    }

    public boolean updateEmployee(EmployeeDTO employee) {
        // Get current employee data
        EmployeeDTO currentEmployee = employeeDAO.getEmployeeById(employee.getEmployeeId());

        // Check if the employee exists
        if (currentEmployee == null) {
            return false;
        }

        // Check if username is being changed and if the new username already exists
        if (!currentEmployee.getUsername().equals(employee.getUsername()) &&
                employeeDAO.isUsernameExists(employee.getUsername())) {
            return false;
        }
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(String employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }

    public EmployeeDTO login(String username, String password) {
        return employeeDAO.login(username, password);
    }

    public String generateNewEmployeeId() {
        List<EmployeeDTO> employees = employeeDAO.getAllEmployees();
        int maxId = 0;

        for (EmployeeDTO employee : employees) {
            String idStr = employee.getEmployeeId();
            if (idStr.startsWith("NV")) {
                try {
                    int id = Integer.parseInt(idStr.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }

        return "NV" + String.format("%03d", maxId + 1);
    }

    // Phương thức tìm kiếm nhân viên theo từ khóa
    public List<EmployeeDTO> searchEmployees(String keyword) {
        List<EmployeeDTO> allEmployees = getAllEmployees();
        List<EmployeeDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allEmployees;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (EmployeeDTO employee : allEmployees) {
            if (employee.getEmployeeId().toLowerCase().contains(lowercaseKeyword) ||
                    employee.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                    employee.getUsername().toLowerCase().contains(lowercaseKeyword) ||
                    employee.getPhone().toLowerCase().contains(lowercaseKeyword) ||
                    (employee.getPositionId() != null
                            && employee.getPositionId().toLowerCase().contains(lowercaseKeyword))) {
                results.add(employee);
            }
        }

        return results;
    }

    public boolean testAddEmployee() {
        System.out.println("\n===== TEST ADD EMPLOYEE =====");
        String testId = "NVTEST";
        String testUsername = "test_user_" + System.currentTimeMillis();
        String testPassword = "test123";
        String testFirstName = "Test"; // Tên
        String testLastName = "User"; // Họ
        String testPositionId = "CV003"; // Nhân viên bán hàng
        String testPhone = "0123456789";

        System.out.println("Creating test employee: " + testId + ", username: " + testUsername);
        System.out.println("First name (tên): " + testFirstName);
        System.out.println("Last name (họ): " + testLastName);

        // Kiểm tra nếu ID đã tồn tại
        EmployeeDTO existingEmployee = employeeDAO.getEmployeeById(testId);
        if (existingEmployee != null) {
            System.out.println("Test employee ID already exists, will attempt to delete first...");
            boolean deleteResult = employeeDAO.deleteEmployee(testId);
            System.out.println("Delete result: " + deleteResult);
        }

        // Kiểm tra nếu username đã tồn tại
        boolean usernameExists = employeeDAO.isUsernameExists(testUsername);
        System.out.println("Username exists before adding: " + usernameExists);

        // Thực hiện thêm
        boolean addResult = addEmployee(testId, testUsername, testPassword, testFirstName, testLastName, testPositionId,
                testPhone);
        System.out.println("Add employee result: " + addResult);

        // Kiểm tra lại sau khi thêm
        existingEmployee = employeeDAO.getEmployeeById(testId);
        if (existingEmployee != null) {
            System.out.println("Successfully retrieved added employee:");
            System.out.println("  ID: " + existingEmployee.getEmployeeId());
            System.out.println("  Username: " + existingEmployee.getUsername());
            System.out.println("  First name (tên): " + existingEmployee.getFirstName());
            System.out.println("  Last name (họ): " + existingEmployee.getLastName());
            System.out.println("  Full name: " + existingEmployee.getFullName());
        } else {
            System.out.println("Failed to retrieve added employee!");
        }

        // Kiểm tra username sau khi thêm
        usernameExists = employeeDAO.isUsernameExists(testUsername);
        System.out.println("Username exists after adding: " + usernameExists);

        // Xóa nhân viên test
        System.out.println("Deleting test employee...");
        boolean deleteResult = employeeDAO.deleteEmployee(testId);
        System.out.println("Delete result: " + deleteResult);

        System.out.println("===== END TEST ADD EMPLOYEE =====\n");
        return addResult;
    }

    public boolean isUsernameExists(String username) {
        return employeeDAO.isUsernameExists(username);
    }

    // Phương thức kiểm tra và sửa lỗi cơ sở dữ liệu
    public boolean repairDatabase() {
        System.out.println("Đang kiểm tra và sửa lỗi cơ sở dữ liệu...");

        // Kiểm tra cấu trúc bảng
        employeeDAO.debugEmployeeTable();

        // Thêm dữ liệu mẫu để kiểm tra
        boolean testResult = employeeDAO.insertTestData();

        System.out.println("Kết quả kiểm tra thêm dữ liệu mẫu: " + (testResult ? "THÀNH CÔNG" : "THẤT BẠI"));
        return testResult;
    }
}