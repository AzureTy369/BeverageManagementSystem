package DTO;

public class EmployeeDTO {
    private String employeeId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String positionId;
    private String phone;

    public EmployeeDTO() {
    }

    public EmployeeDTO(String employeeId, String username, String password, String firstName,
            String lastName, String positionId, String phone) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.positionId = positionId;
        this.phone = phone;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }
}