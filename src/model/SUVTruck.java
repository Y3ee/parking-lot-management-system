package model;

public class SUVTruck extends Vehicle {
    public SUVTruck(String plateNumber) {
        super(plateNumber, "SUV_TRUCK");
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.REGULAR; // strict
    }
}
