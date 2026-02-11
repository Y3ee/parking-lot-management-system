package service;

import dao.ParkingSpotDAO;
import dao.VehicleDAO;
import java.util.List;
import model.ParkingSpot;
import model.Vehicle; // This was likely missing!

public class ParkingService {

    private static ParkingService instance;
    private VehicleDAO vehicleDAO;
    private ParkingSpotDAO spotDAO;

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

    public boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = spotDAO.findAvailableSpot(vehicle.getType());
        if (spot == null) {
            System.out.println("Parking Failed: No spots available.");
            return false;
        }
        vehicleDAO.saveVehicle(vehicle, spot.getSpotId());
        spotDAO.markOccupied(spot.getSpotId());
        return true;
    }

    // THIS is the method AdminPanel was looking for
    public List<ParkingSpot> getParkingLotStatus() {
        return spotDAO.getAllSpots();
    }

    public boolean parkVehicleAt(Vehicle vehicle, String spotId) {
        // 1. Double check if the spot is actually available (prevent race conditions)
        model.ParkingSpot spot = spotDAO.getAllSpots().stream()
                .filter(s -> s.getSpotId().equals(spotId))
                .findFirst()
                .orElse(null);

        if (spot == null || spot.isOccupied()) {
            System.out.println("Parking Failed: Spot is occupied or invalid.");
            return false;
        }

        // 2. Save the vehicle
        vehicleDAO.saveVehicle(vehicle, spotId);
        
        // 3. Update the spot status
        spotDAO.markOccupied(spotId);
        return true;
    }
}