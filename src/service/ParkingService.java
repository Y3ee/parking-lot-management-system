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
}