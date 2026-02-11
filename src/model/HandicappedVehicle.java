package model;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plateNumber) {
        super(plateNumber, "HANDICAPPED");
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return true; // can park anywhere (billing/discount later)
    }
}
