package service;

import dao.ParkingSpotDAO;
import dao.PaymentDAO;
import dao.VehicleDAO;
import model.ActiveParkingRecord;
import model.ExitBill;
import model.Receipt;
import model.SpotType;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExitService {

    private static ExitService instance;

    private final VehicleDAO vehicleDAO;
    private final ParkingSpotDAO spotDAO;
    private final FineService fineService;
    private final PaymentDAO paymentDAO;

    private ExitService() {
        this.vehicleDAO = new VehicleDAO();
        this.spotDAO = new ParkingSpotDAO();
        this.fineService = FineService.getInstance();
        this.paymentDAO = new PaymentDAO();
    }

    public static ExitService getInstance() {
        if (instance == null) instance = new ExitService();
        return instance;
    }

    // 1) Preview bill (NO DB updates)
    public ExitBill previewBill(String plate) {
        String p = normalizePlate(plate);

        ActiveParkingRecord rec = vehicleDAO.findActiveParkingRecord(p);
        if (rec == null) throw new IllegalArgumentException("Vehicle not found / not currently parked.");

        LocalDateTime now = LocalDateTime.now();
        long hours = ceilHours(rec.getEntryTime(), now);

        SpotType spotType = rec.getSpotType();
        double hourlyRate = spotType.getRate();
        double parkingFee = hours * hourlyRate;

        double unpaid = fineService.getUnpaidFines(p);

        double newFine = 0.0;
        newFine += fineService.calculateOverstayFine(hours);
        newFine += fineService.calculateReservedMisuseFine(p, spotType);

        double totalDue = parkingFee + unpaid + newFine;

        return new ExitBill(
                p,
                rec.getEntryTime(),
                now,
                hours,
                rec.getSpotId(),
                spotType,
                hourlyRate,
                parkingFee,
                unpaid,
                newFine,
                totalDue
        );
    }

    // 2) Pay ONLY parking fee (fines stay unpaid and carry forward)
    public Receipt payParkingOnlyAndExit(String plate, String paymentMethod) {
        ExitBill bill = previewBill(plate);

        // ✅ only insert fines now (NOT during preview)
        if (bill.getNewFines() > 0) {
            fineService.addFine(bill.getPlate(), bill.getNewFines()); // unpaid
        }

        // vehicle exit + paid only parking fee
        vehicleDAO.updateExitAndFee(bill.getPlate(), bill.getExitTime(), bill.getParkingFee());

        // free spot
        spotDAO.markAvailable(bill.getSpotId());

        // revenue: only paid amount
        ParkingService.getInstance().addRevenue(bill.getParkingFee());

        double remaining = bill.getTotalDue() - bill.getParkingFee();
        Receipt receipt = new Receipt(bill, safeMethod(paymentMethod), bill.getParkingFee(), remaining);

        // ✅ store receipt/payment in DB
        paymentDAO.saveReceipt(receipt);

        return receipt;
    }

    // 3) Pay ALL (parking + previous unpaid fines + new fines)
    public Receipt payAllAndExit(String plate, String paymentMethod) {
        ExitBill bill = previewBill(plate);

        // ✅ insert new fine record then mark all fines paid
        if (bill.getNewFines() > 0) {
            fineService.addFine(bill.getPlate(), bill.getNewFines());
        }
        fineService.markAllPaid(bill.getPlate());

        // vehicle exit + paid total
        vehicleDAO.updateExitAndFee(bill.getPlate(), bill.getExitTime(), bill.getTotalDue());

        // free spot
        spotDAO.markAvailable(bill.getSpotId());

        ParkingService.getInstance().addRevenue(bill.getTotalDue());

        Receipt receipt = new Receipt(bill, safeMethod(paymentMethod), bill.getTotalDue(), 0.0);

        // ✅ store receipt/payment in DB
        paymentDAO.saveReceipt(receipt);

        return receipt;
    }

    // ---------------- helpers ----------------

    private String normalizePlate(String plate) {
        String p = plate == null ? "" : plate.trim();
        if (p.isEmpty()) throw new IllegalArgumentException("Plate number cannot be empty.");
        return p.toUpperCase();
    }

    private long ceilHours(LocalDateTime start, LocalDateTime end) {
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0) return 0;
        return (minutes + 59) / 60;
    }

    private String safeMethod(String method) {
        if (method == null) return "UNKNOWN";
        String m = method.trim().toUpperCase();
        return m.isEmpty() ? "UNKNOWN" : m;
    }
}
