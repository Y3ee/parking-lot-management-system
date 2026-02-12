package model;

public class ProgressiveFineScheme implements FineScheme {
    @Override
    public double calculateOverstayFine(long totalHoursParked) {
        if (totalHoursParked <= 24) return 0.0;

        double fine = 50.0;
        if (totalHoursParked > 48) fine += 100.0;
        if (totalHoursParked > 72) fine += 150.0;
        if (totalHoursParked > 72) fine += 200.0; // "Above 72"
        return fine;
    }

    @Override
    public String getName() { return "Progressive"; }
}
