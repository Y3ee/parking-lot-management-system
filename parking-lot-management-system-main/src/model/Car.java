package model;

public class Car extends Vehicle {
    public Car(String plateNumber) {
        super(plateNumber, "Car");
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.COMPACT || spotType == SpotType.REGULAR;
    }
}
