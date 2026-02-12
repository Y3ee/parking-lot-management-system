package model;

public class FixedFineScheme implements FineScheme {
    @Override
    public double calculateOverstayFine(long totalHoursParked) {
        return totalHoursParked > 24 ? 50.0 : 0.0;
    }

    @Override
    public String getName() { return "Fixed (RM50)"; }
}
