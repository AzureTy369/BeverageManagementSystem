package model;

import java.math.BigDecimal;

public class Position {
    private String positionId;
    private String positionName;
    private BigDecimal salary;

    public Position() {
    }

    public Position(String positionId, String positionName, BigDecimal salary) {
        this.positionId = positionId;
        this.positionName = positionName;
        this.salary = salary;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return positionName;
    }
}