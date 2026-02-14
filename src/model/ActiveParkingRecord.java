package model;

import java.time.LocalDateTime;

public class ActiveParkingRecord {
    private final String plate;
    private final String vehicleType;
    private final LocalDateTime entryTime;
    private final String spotId;
    private final SpotType spotType;
    private final boolean isOkuCardholder;

    public ActiveParkingRecord(String plate, String vehicleType, LocalDateTime entryTime, String spotId, SpotType spotType, boolean isOkuCardholder) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
        this.spotId = spotId;
        this.spotType = spotType;
        this.isOkuCardholder = isOkuCardholder;
    }

    public String getPlate() { return plate; }
    public String getVehicleType() { return vehicleType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
    public boolean isOkuCardholder() { return isOkuCardholder; }
}
