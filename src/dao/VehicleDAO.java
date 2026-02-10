package dao;

import database.DatabaseConnection;
import model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class VehicleDAO {

    private Connection conn;

    public VehicleDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public void saveVehicle(Vehicle v, String spotId) {
        String sql = """
            INSERT INTO vehicle (plate_number, vehicle_type, entry_time, spot_id)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getPlateNumber());
            ps.setString(2, v.getVehicleType());
            ps.setString(3, v.getEntryTime().toString());
            ps.setString(4, spotId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
