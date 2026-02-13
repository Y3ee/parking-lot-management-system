package model;

import java.time.LocalDateTime;


public class RevenueReport {

    private final LocalDateTime generatedAt;

    private final double totalParkingRevenue;
    private final double totalFineRevenue;
    private final double totalRevenue;

    private final int totalTransactions;
    private final int totalVehiclesServed;
    private final double todayRevenue;
    private final double weekRevenue;
    private final double monthRevenue;

    public RevenueReport(double totalParkingRevenue, 
                            double totalFineRevenue, 
                            int totalTransactions, 
                            int totalVehiclesServed,
                            double todayRevenue,
                            double weekRevenue,
                            double monthRevenue) {

        this.generatedAt = LocalDateTime.now();
        this.totalParkingRevenue = totalParkingRevenue;
        this.totalFineRevenue = totalFineRevenue;
        this.totalRevenue = totalParkingRevenue + totalFineRevenue;
        this.totalTransactions = totalTransactions;
        this.totalVehiclesServed = totalVehiclesServed;
        this.todayRevenue = todayRevenue;
        this.weekRevenue = weekRevenue;
        this.monthRevenue = monthRevenue;
    }

    public LocalDateTime getGeneratedAt() {return generatedAt; }
    public double getTotalParkingRevenue() { return totalParkingRevenue; }
    public double getTotalFineRevenue() { return totalFineRevenue; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalTransactions() { return totalTransactions; }
    public int getTotalVehiclesServed() { return totalVehiclesServed; }
    public double getTodayRevenue() { return todayRevenue; }
    public double getWeekRevenue() { return weekRevenue; }
    public double getMonthRevenue() { return monthRevenue; }

    public double getAverageRevenuePerTransaction() {
        return totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
    }
    
    public String getSummary(){
        return String.format(
            "=== REVENUE REPORT ===\n" +
            "Generated: %s\n\n" +
            "Total Revenue: RM %.2f\n" +
            "  - Parking Fees: RM %.2f\n" +
            "  - Fine Collections: RM %.2f\n\n" +
            "Transactions: %d\n" +
            "Vehicles Served: %d\n" +
            "Avg per Transaction: RM %.2f\n\n" +
            "Period Breakdown:\n" +
            "  - Today: RM %.2f\n" +
            "  - This Week: RM %.2f\n" +
            "  - This Month: RM %.2f",
            generatedAt.toString(),
            totalRevenue, totalParkingRevenue, totalFineRevenue,
            totalTransactions, totalVehiclesServed,
            getAverageRevenuePerTransaction(),
            todayRevenue, weekRevenue, monthRevenue
        );
    }
}
