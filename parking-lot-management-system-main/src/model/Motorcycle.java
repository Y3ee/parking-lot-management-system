package model;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plateNumber) {
        super(plateNumber, "Motorcycle");
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.COMPACT;
    }
}
