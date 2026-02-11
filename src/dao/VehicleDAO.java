package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Vehicle;

public class VehicleDAO {

    public void saveVehicle(Vehicle v, String spotId) {
        String sql = "INSERT INTO vehicle (plate_number, vehicle_type, entry_time, spot_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getPlateNumber());
            ps.setString(2, v.getVehicleType());
            ps.setString(3, v.getEntryTime().toString());
            ps.setString(4, spotId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentlyParked(String plate) {
    String sql = "SELECT 1 FROM vehicle WHERE plate_number = ? AND exit_time IS NULL LIMIT 1";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, plate);

        ResultSet rs = ps.executeQuery();
        return rs.next(); // true if found active record

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
