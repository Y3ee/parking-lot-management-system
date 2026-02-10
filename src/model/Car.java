package model;

// 1. "extends Vehicle" fixes "Type mismatch: cannot convert from Car to Vehicle"
public class Car extends Vehicle { 

    // 2. This Constructor fixes "The constructor Car(String) is undefined"
    public Car(String plateNumber) {
        // We pass the plate AND the correct spot type to the parent Vehicle class
        super(plateNumber, SpotType.REGULAR); 
    }
}