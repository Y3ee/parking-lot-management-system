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

    // ✅ FIX: remove getType() usage. Use polymorphism vehicle.canParkIn(spot.getType())
    // Option 1: change return type to Ticket (recommended)
    public model.Ticket parkVehicle(Vehicle vehicle) {

        if (vehicleDAO.isCurrentlyParked(vehicle.getPlateNumber())) {
            System.out.println("Parking Failed: Vehicle already parked.");
            return null;
        }

        ParkingSpot spot = spotDAO.getAllSpots().stream()
                .filter(s -> !s.isOccupied())
                .filter(s -> vehicle.canParkIn(s.getType()))
                .findFirst()
                .orElse(null);

        if (spot == null) {
            System.out.println("Parking Failed: No suitable spots available.");
            return null;
        }

        vehicleDAO.saveVehicle(vehicle, spot.getSpotId());
        spotDAO.markOccupied(spot.getSpotId());

        return generateTicket(vehicle, spot.getSpotId());
    }


    // AdminPanel uses this
    public List<ParkingSpot> getParkingLotStatus() {
        return spotDAO.getAllSpots();
    }

    // Your EntryPanel uses this (user selects spot)
    public model.Ticket parkVehicleAt(Vehicle vehicle, String spotId) {

        if (vehicleDAO.isCurrentlyParked(vehicle.getPlateNumber())) {
            System.out.println("Parking Failed: Vehicle already parked.");
            return null;
        }

        ParkingSpot spot = spotDAO.getAllSpots().stream()
                .filter(s -> s.getSpotId().equals(spotId))
                .findFirst()
                .orElse(null);

        if (spot == null || spot.isOccupied()) return null;

        if (!vehicle.canParkIn(spot.getType())) return null;

        vehicleDAO.saveVehicle(vehicle, spotId);
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
