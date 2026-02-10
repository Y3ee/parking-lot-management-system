package model;

public class ParkingSpot {

    private String spotId;
    private String type;
    private boolean occupied;
    private double hourlyRate;

    public ParkingSpot(String spotId, String type, double hourlyRate) {
        this.spotId = spotId;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.occupied = false;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getType() {
        return type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void occupy() {
        occupied = true;
    }

    public void release() {
        occupied = false;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
