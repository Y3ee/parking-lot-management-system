package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ParkingSpotDAO {

    private Connection conn;

    public ParkingSpotDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public void markOccupied(String spotId) {
        String sql = "UPDATE parking_spot SET status = 'OCCUPIED' WHERE spot_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
