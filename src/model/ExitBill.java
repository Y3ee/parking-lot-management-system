package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ExitBill {

    private final String plate;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final long hours;
    private final String spotId;
    private final SpotType spotType;
    private final double hourlyRate;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

    private final double parkingFee;
    private final double unpaidFines;
    private final double newFines;
    private final double totalDue;

    public ExitBill(String plate,
                    LocalDateTime entryTime,
                    LocalDateTime exitTime,
                    long hours,
                    String spotId,
                    SpotType spotType,
                    double hourlyRate,
                    double parkingFee,
                    double unpaidFines,
                    double newFines,
                    double totalDue) {

        this.plate = plate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hours = hours;
        this.spotId = spotId;
        this.spotType = spotType;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.unpaidFines = unpaidFines;
        this.newFines = newFines;
        this.totalDue = totalDue;
    }

    public String getPlate() { return plate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getHours() { return hours; }

    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
    public double getHourlyRate() { return hourlyRate; }

    public double getParkingFee() { return parkingFee; }
    public double getUnpaidFines() { return unpaidFines; }
    public double getNewFines() { return newFines; }
    public double getTotalFinesDue() { return unpaidFines + newFines; }
    public double getTotalDue() { return totalDue; }

    // Helpful for "Preview Bill"
    public String prettyBill() {
        return ""
            + "=== EXIT BILL PREVIEW ===\n"
            + "Plate: " + plate + "\n"
            + "Spot : " + spotId + " (" + spotType + ")\n"
            + "Entry Time: " + entryTime.format(DT_FMT) + "\n"
            + "Exit Time : " + exitTime.format(DT_FMT) + "\n"
            + "Duration : " + hours + "\n\n"
            + "Parking Fee : " + hours + " x RM " + String.format("%.2f", hourlyRate)
            + " = RM " + String.format("%.2f", parkingFee) + "\n"
            + "Unpaid Fines (previous): RM " + String.format("%.2f", unpaidFines) + "\n"
            + "New Fines (this visit) : RM " + String.format("%.2f", newFines) + "\n"
            + "Total Fines Due        : RM " + String.format("%.2f", getTotalFinesDue()) + "\n\n"
            + "TOTAL PAYMENT DUE: RM " + String.format("%.2f", totalDue) + "\n";
    }
}
