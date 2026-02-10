package model;

public class ParkingSpot {

    private String spotId;
    private SpotType type;      // Use Enum, not String
    private boolean occupied;
    private String vehiclePlate; // <--- ADD THIS (Required by )

    // Notice we DON'T pass hourlyRate anymore. The Enum handles it.
    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.occupied = false;
        this.vehiclePlate = null;
    }

    public double getHourlyRate() {
        return type.getRate(); // <--- This fulfills "Future Proofing"
    }

    public void occupy(String plate) { // <--- Update this to take the plate
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