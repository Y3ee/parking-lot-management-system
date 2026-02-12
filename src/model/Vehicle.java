package model;

import java.time.LocalDateTime;

public abstract class Vehicle {
    protected String plateNumber;
    protected String vehicleType;       // e.g. Car, Motorcycle, SUV_TRUCK, HANDICAPPED
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;   // null if parked

    public Vehicle(String plateNumber, String vehicleType) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
    }

    public abstract boolean canParkIn(SpotType spotType);

    public String getPlateNumber() { return plateNumber; }
    public String getVehicleType() { return vehicleType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
}
