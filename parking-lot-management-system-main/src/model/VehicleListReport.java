package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class VehicleListReport {

    private final List<ParkedVehicle> parkedVehicles;
    private final int totalCount;

    public VehicleListReport(List<ParkedVehicle> parkedVehicles) {
        this.parkedVehicles = parkedVehicles;
        this.totalCount = parkedVehicles.size();
    }

    public List<ParkedVehicle> getParkedVehicles() { return parkedVehicles; }
    public int getTotalCount() { return totalCount; }
    
    public static class ParkedVehicle {
        private final String plateNumber;
        private final String vehicleType;
        private final String spotId;
        private final SpotType spotType;
        private final LocalDateTime entryTime;
        private final long hoursParked;
        private final double currentFee;

        public ParkedVehicle(String plateNumber, String vehicleType,
                             String spotId, SpotType spotType,
                             LocalDateTime entryTime) {
            this.plateNumber = plateNumber;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;

            long minutes = Duration.between(entryTime, LocalDateTime.now()).toMinutes();
            this.hoursParked = minutes > 0 ? (minutes + 59) / 60 : 0; // round up to next hour
            this.currentFee = hoursParked * spotType.getRate();
        }

        public String getPlateNumber() { return plateNumber; }
        public String getVehicleType() { return vehicleType; }
        public String getSpotId() { return spotId; }
        public SpotType getSpotType() { return spotType; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public long getHoursParked() { return hoursParked; }
        public double getCurrentFee() { return currentFee; }
    }
    
}
