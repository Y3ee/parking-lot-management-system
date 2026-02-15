package service;

import dao.ParkingSpotDAO;
import dao.PaymentDAO;
import dao.VehicleDAO;
import java.time.Duration;
import java.time.LocalDateTime;
import model.ActiveParkingRecord;
import model.ExitBill;
import model.Receipt;
import model.SpotType;

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

    // 1. preview bill (NO DB updates)
    public ExitBill previewBill(String plate) {
        String p = normalizePlate(plate);

        ActiveParkingRecord rec = vehicleDAO.findActiveParkingRecord(p);
        if (rec == null) throw new IllegalArgumentException("Vehicle not found / not currently parked.");

        LocalDateTime now = LocalDateTime.now();
        long hours = ceilHours(rec.getEntryTime(), now);

        SpotType spotType = rec.getSpotType();
        boolean isHandicappedVehicle = "HANDICAPPED".equalsIgnoreCase(rec.getVehicleType());
        boolean hasCard = rec.isOkuCardholder();

        double hourlyRate;
        double parkingFee;

        if (isHandicappedVehicle) {
            if (spotType == SpotType.HANDICAPPED) {
                if (hasCard) {
                    hourlyRate = 0.0;          // free
                    parkingFee = 0.0;
                } else {
                    hourlyRate = 2.0;          // no card but handicapped vehicle in handicapped spot -> RM2/hr
                    parkingFee = hours * hourlyRate;
                }
            } else {
                if (hasCard) {
                    hourlyRate = 2.0;          // card in other spot -> RM2/hr
                    parkingFee = hours * hourlyRate;
                } else {
                    hourlyRate = spotType.getRate(); // no card in other spot -> normal rate
                    parkingFee = hours * hourlyRate;
                }
            }
        } else {
            hourlyRate = spotType.getRate();
            parkingFee = hours * hourlyRate;
        }
        
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

    // 2. pay ONLY parking fee (fines stay unpaid and carry forward)
    public Receipt payParkingOnlyAndExit(String plate, String paymentMethod) {
        ExitBill bill = previewBill(plate);

        // only insert fines now (NOT during preview)
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

    // 3. pay ALL (parking + previous unpaid fines + new fines)
    public Receipt payAllAndExit(String plate, String paymentMethod) {
        ExitBill bill = previewBill(plate);

        // insert new fine record then mark all fines paid
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

        // store receipt/payment in DB
        paymentDAO.saveReceipt(receipt);

        return receipt;
    }

    // helper methods 
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
