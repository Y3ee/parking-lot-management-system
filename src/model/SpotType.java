package model;

public enum SpotType {
    COMPACT(2.0),       // RM 2/hour
    REGULAR(5.0),       // RM 5/hour
    HANDICAPPED(2.0),   // RM 2/hour
    RESERVED(10.0);     // RM 10/hour

    private final double hourlyRate;

    SpotType(double rate) {
        this.hourlyRate = rate;
    }

    public double getRate() {
        return hourlyRate;
    }
}