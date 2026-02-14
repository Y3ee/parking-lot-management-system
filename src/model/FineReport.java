package model;

import java.util.List;

public class FineReport {
    private final double totalUnpaidFines;
    private final double totalPaidFines;
    private final int numberOfVehiclesWithFines;
    private final List<OutstandingFine> outstandingFines;

    public FineReport(double totalUnpaidFines, double totalPaidFines,
                      int numberOfVehiclesWithFines,
                      List<OutstandingFine> outstandingFines) {
        this.totalUnpaidFines = totalUnpaidFines;
        this.totalPaidFines = totalPaidFines;
        this.numberOfVehiclesWithFines = numberOfVehiclesWithFines;
        this.outstandingFines = outstandingFines;
    }

    public double getTotalPaidFines() { return totalPaidFines; }
    public double getTotalUnpaidFines() { return totalUnpaidFines; }
    public int getNumberOfVehiclesWithFines() { return numberOfVehiclesWithFines; }
    public List<OutstandingFine> getOutstandingFines() { return outstandingFines; }
    public double getTotalFines() { return totalUnpaidFines + totalPaidFines;}

    public double getCollectionRate() { 
        double total = getTotalFines();
        return total > 0 ? (totalPaidFines / total * 100) : 100.0; 
    }

    //vehicle with outstanding fine 
    public static class OutstandingFine {
        private final String plateNumber;
        private final double amount;
        private final int fineCount;

        public OutstandingFine(String plateNumber, double amount, int fineCount) {
            this.plateNumber = plateNumber;
            this.amount = amount;
            this.fineCount = fineCount;
        }

        public String getPlateNumber() { return plateNumber; }
        public double getAmount() { return amount; }
        public int getFineCount() { return fineCount; }
    }
}
