package service;

import dao.FineDAO;
import dao.ParkingSpotDAO;
import model.FineScheme;
import model.FixedFineScheme;
import model.SpotType;

public class FineService {

    private static FineService instance;

    private FineScheme scheme; 
    private final FineDAO fineDAO;
    private final ParkingSpotDAO spotDAO;

    private FineService() {
        this.scheme = new FixedFineScheme(); // default scheme
        this.fineDAO = new FineDAO();
        this.spotDAO = new ParkingSpotDAO();
    }

    public static FineService getInstance() {
        if (instance == null) instance = new FineService();
        return instance;
    }

    public FineScheme getScheme() { return scheme; }

    public void setScheme(FineScheme scheme) {
        if (scheme != null) this.scheme = scheme;
    }

    public double getUnpaidFines(String plate) {
        return fineDAO.getUnpaidTotal(norm(plate));
    }

    public double calculateOverstayFine(long totalHoursParked) {
        return scheme.calculateOverstayFine(totalHoursParked);
    }

    public double calculateReservedMisuseFine(String plate, SpotType spotType) {
        // reserved without reservation system: use VIP whitelist rule
        if (spotType == SpotType.RESERVED && !spotDAO.isVip(norm(plate))) {
            return 50.0;
        }
        return 0.0;
    }

    public void addFine(String plate, double amount) {
        fineDAO.addFine(norm(plate), amount);
    }

    public void markAllPaid(String plate) {
        fineDAO.markAllPaid(norm(plate));
    }

    private String norm(String plate) {
        return plate == null ? "" : plate.trim().toUpperCase();
    }
}
