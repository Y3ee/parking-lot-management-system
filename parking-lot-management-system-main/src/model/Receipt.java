package model;
import java.time.format.DateTimeFormatter;


public class Receipt {

    private final ExitBill bill;
    private final String paymentMethod;
    private final double totalPaid;
    private final double remainingBalance;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

    public Receipt(ExitBill bill, String paymentMethod, double totalPaid, double remainingBalance) {
        this.bill = bill;
        this.paymentMethod = paymentMethod;
        this.totalPaid = totalPaid;
        this.remainingBalance = remainingBalance;
    }

    public ExitBill getBill() { return bill; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalPaid() { return totalPaid; }
    public double getRemainingBalance() { return remainingBalance; }

    // REQUIRED receipt output
    public String prettyReceipt() {
        return ""
            + "=========== RECEIPT ===========\n"
            + "Plate: " + bill.getPlate() + "\n"
            + "Spot : " + bill.getSpotId() + " (" + bill.getSpotType() + ")\n\n"
            + "Entry Time: " + bill.getEntryTime().format(DT_FMT) + "\n"
            + "Exit Time : " + bill.getExitTime().format(DT_FMT) + "\n"
            + "Duration (hours): " + bill.getHours() + "\n\n"
            + "Parking Fee Breakdown:\n"
            + "  " + bill.getHours() + " x RM " + String.format("%.2f", bill.getHourlyRate())
            + " = RM " + String.format("%.2f", bill.getParkingFee()) + "\n\n"
            + "Fines Due:\n"
            + "  Unpaid (previous): RM " + String.format("%.2f", bill.getUnpaidFines()) + "\n"
            + "  New (this visit) : RM " + String.format("%.2f", bill.getNewFines()) + "\n"
            + "  Total fines      : RM " + String.format("%.2f", bill.getTotalFinesDue()) + "\n\n"
            + "TOTAL AMOUNT PAID: RM " + String.format("%.2f", totalPaid) + "\n"
            + "PAYMENT METHOD: " + paymentMethod + "\n"
            + "REMAINING BALANCE: RM " + String.format("%.2f", remainingBalance) + "\n"
            + "================================\n";
    }
}
