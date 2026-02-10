package model;

import java.time.LocalDateTime;

public class Vehicle {

    protected String plateNumber;
    protected String vehicleType;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;

    public Vehicle(String plateNumber, String vehicleType) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}
