package BUS;

import DAO.EmployeeDAO;
import DTO.Employee;
import java.util.List;

public class EmployeeController {
    private EmployeeDAO employeeDAO;

    public EmployeeController() {
        employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }

    public Employee getEmployeeByUsername(String username) {
        return employeeDAO.getEmployeeByUsername(username);
    }

    public boolean addEmployee(String employeeId, String username, String password,
            String firstName, String lastName, String positionId, String phone) {
        // Check if username already exists
        if (employeeDAO.isUsernameExists(username)) {
            return false;
        }

        Employee employee = new Employee(employeeId, username, password, firstName, lastName, positionId, phone);
        return employeeDAO.addEmployee(employee);
    }

    public boolean addEmployee(Employee employee) {
        // Check if username already exists
        if (employeeDAO.isUsernameExists(employee.getUsername())) {
            return false;
        }
        return employeeDAO.addEmployee(employee);
    }

    public boolean updateEmployee(String employeeId, String username, String password,
            String firstName, String lastName, String positionId, String phone) {
        // Get current employee data
        Employee currentEmployee = employeeDAO.getEmployeeById(employeeId);

        // Check if username is being changed and if the new username already exists
        if (!currentEmployee.getUsername().equals(username) && employeeDAO.isUsernameExists(username)) {
            return false;
        }

        Employee employee = new Employee(employeeId, username, password, firstName, lastName, positionId, phone);
        return employeeDAO.updateEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        // Get current employee data
        Employee currentEmployee = employeeDAO.getEmployeeById(employee.getEmployeeId());

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

    public Employee login(String username, String password) {
        return employeeDAO.login(username, password);
    }
}