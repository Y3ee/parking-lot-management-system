package model;

import java.time.LocalDateTime;

public class ActiveParkingRecord {
    private final String plate;
    private final LocalDateTime entryTime;
    private final String spotId;
    private final SpotType spotType;

    public ActiveParkingRecord(String plate, LocalDateTime entryTime, String spotId, SpotType spotType) {
        this.plate = plate;
        this.entryTime = entryTime;
        this.spotId = spotId;
        this.spotType = spotType;
    }

    public String getPlate() { return plate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
}
