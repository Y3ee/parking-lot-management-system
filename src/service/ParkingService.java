package service;

import dao.ParkingSpotDAO;
import dao.VehicleDAO;
import java.util.List;
import model.ParkingSpot;
import model.Vehicle;


public class ParkingService {

    private static ParkingService instance;
    private VehicleDAO vehicleDAO;
    private ParkingSpotDAO spotDAO;
    private double totalRevenue = 0.0;


    private ParkingService() {
        vehicleDAO = new VehicleDAO();
        spotDAO = new ParkingSpotDAO();
        spotDAO.initializeParkingLot();
    }

    public static ParkingService getInstance() {
        if (instance == null) {
            instance = new ParkingService();
        }
        return instance;
    }

    // AdminPanel uses this
    public List<ParkingSpot> getParkingLotStatus() {
        return spotDAO.getAllSpots();
    }

    // EntryPanel uses this 
    public model.Ticket parkVehicleAt(Vehicle vehicle, String spotId, boolean isOkuCardholder) {

        if (vehicleDAO.isCurrentlyParked(vehicle.getPlateNumber())) {
            System.out.println("Parking Failed: Vehicle already parked.");
            return null;
        }

        ParkingSpot spot = spotDAO.getAllSpots().stream()
                .filter(s -> s.getSpotId().equals(spotId))
                .findFirst()
                .orElse(null);

        if (spot == null || spot.isOccupied()) return null;

        boolean isVip = spotDAO.isVip(vehicle.getPlateNumber());

        // VIP logic unchanged
        if (spot.getType() == model.SpotType.RESERVED && !isVip) {
            System.out.println("Parking Failed: RESERVED spot requires VIP.");
            return null;
        }

        // normal suitability (VIP reserved bypass still allowed)
        if (!(spot.getType() == model.SpotType.RESERVED && isVip)) {
            if (!vehicle.canParkIn(spot.getType())) {
                System.out.println("Parking Failed: Vehicle type not suitable for this spot.");
                return null;
            }
        }

        // save vehicle with OKU flag
        vehicleDAO.saveVehicle(vehicle, spotId, isOkuCardholder);
        spotDAO.markOccupied(spotId);

        return generateTicket(vehicle, spotId);
    }

    private model.Ticket generateTicket(Vehicle v, String spotId) {
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String ticketId = "T-" + v.getPlateNumber() + "-" + v.getEntryTime().format(fmt);
        return new model.Ticket(ticketId, spotId, v.getEntryTime());
    }

    public ExitService getExitService() {
    return ExitService.getInstance();
}

public FineService getFineService() {
    return FineService.getInstance();
}

public void addRevenue(double amount) {
    totalRevenue += amount;
}

public double getTotalRevenue() {
    return totalRevenue;
}

}
