package model;

import java.time.LocalDateTime;

// 1. Make it ABSTRACT so you can't just create a generic "Vehicle"
public abstract class Vehicle {
    protected String plateNumber;
    protected SpotType type; // Use the Enum!
    protected LocalDateTime entryTime;

    public Vehicle(String plateNumber, SpotType type) {
        this.plateNumber = plateNumber;
        this.type = type;
        this.entryTime = LocalDateTime.now();
    }

    // 2. Abstract method for Polymorphism (Optional but impresses interviewers)
    // e.g., public abstract double calculateRate(); 

    public String getPlateNumber() { return plateNumber; }
    public SpotType getType() { return type; }
    public LocalDateTime getEntryTime() { return entryTime; }
    
    // Add a setter for entryTime for Database loading
    public void setEntryTime(LocalDateTime time) { this.entryTime = time; }
}