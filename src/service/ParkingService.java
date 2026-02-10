package service;

import dao.ParkingSpotDAO;
import dao.VehicleDAO;
import model.Vehicle;

public class ParkingService {

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ParkingSpotDAO spotDAO = new ParkingSpotDAO();

    public void parkVehicle(Vehicle vehicle, String spotId) {
        vehicleDAO.saveVehicle(vehicle, spotId);
        spotDAO.markOccupied(spotId);
    }
}
