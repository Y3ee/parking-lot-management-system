package model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plateNumber) {
        // Motorcycles identify as "Motorcycle" type
        super(plateNumber, SpotType.COMPACT);
    }
}