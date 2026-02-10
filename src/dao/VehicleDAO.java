package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Vehicle;

public class VehicleDAO {

    // Remove the constructor that stores the connection.
    // We will get the connection fresh every time.

    public void saveVehicle(Vehicle v, String spotId) {
        // 1. Define SQL (Matches your new table structure)
        String sql = "INSERT INTO vehicle (plate_number, vehicle_type, entry_time, spot_id) VALUES (?, ?, ?, ?)";

        // 2. Get Connection inside the method
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, v.getPlateNumber());
            
            // FIX: Use getType().toString() because 'type' is now an Enum
            ps.setString(2, v.getType().toString()); 
            
            // Convert LocalDateTime to String for SQLite
            ps.setString(3, v.getEntryTime().toString());
            
            ps.setString(4, spotId);
            
           
            System.out.println("Saving vehicle: " + v.getPlateNumber()); // <--- Debug print
            int rows = ps.executeUpdate();
            System.out.println("Rows inserted: " + rows); // <--- Should say "Rows inserted: 1"

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}