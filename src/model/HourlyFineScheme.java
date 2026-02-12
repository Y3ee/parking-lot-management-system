package model;

public class HourlyFineScheme implements FineScheme {
    @Override
    public double calculateOverstayFine(long totalHoursParked) {
        if (totalHoursParked <= 24) return 0.0;
        return (totalHoursParked - 24) * 20.0;
    }

    @Override
    public String getName() { return "Hourly (RM20/hr after 24h)"; }
}
