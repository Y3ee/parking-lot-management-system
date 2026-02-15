package model;

public class ParkingSpot {

    private String spotId;
    private SpotType type;      
    private boolean occupied;
    private String vehiclePlate; 

    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.occupied = false;
        this.vehiclePlate = null;
    }

    public double getHourlyRate() {
        return type.getRate(); // future proofing
    }

    public void occupy(String plate) { // take the plate
        this.occupied = true;
        this.vehiclePlate = plate;
    }

    public void release() {
        this.occupied = false;
        this.vehiclePlate = null;
    }

    // Getters
    public String getSpotId() { return spotId; }
    public SpotType getType() { return type; }
    public boolean isOccupied() { return occupied; }
    public String getVehiclePlate() { return vehiclePlate; }
}