package DTO;

public class Customer {
    private String customerId;
    private String firstname;
    private String lastname;
    private String address;
    private String phone;
    private String point;

    // Constructors
    public Customer() {
    }

    public Customer(String customerId, String firstname, String lastname, String address, String phone) {
        this.customerId = customerId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.phone = phone;
    }


    // Helper method to set all basic fields
    public void setAll(String id, String first, String last, String address, String phone) {
        this.customerId = id;
        this.firstname = first;
        this.lastname = last;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPoint() {
        return point;
    }

  
}