package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import model.ActiveParkingRecord;
import model.SpotType;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Vehicle;

public class VehicleDAO {

    public void saveVehicle(Vehicle v, String spotId, boolean isOkuCardholder) {

        String sql = """
            INSERT INTO vehicle
            (plate_number, vehicle_type, entry_time, spot_id, is_oku_cardholder)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getPlateNumber().toUpperCase());
            ps.setString(2, v.getVehicleType());
            ps.setString(3, v.getEntryTime().toString());
            ps.setString(4, spotId);
            ps.setInt(5, isOkuCardholder ? 1 : 0);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentlyParked(String plate) {
    String sql = "SELECT 1 FROM vehicle WHERE plate_number = ? AND exit_time IS NULL LIMIT 1";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, plate.toUpperCase());

        ResultSet rs = ps.executeQuery();
        return rs.next(); // true if found active record

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public ActiveParkingRecord findActiveParkingRecord(String plate) {
        String sql = """
            SELECT v.plate_number, v.vehicle_type, v.entry_time, v.spot_id, p.type, COALESCE(v.is_oku_cardholder, 0) as is_oku_cardholder
            FROM vehicle v
            JOIN parking_spot p ON p.spot_id = v.spot_id
            WHERE v.plate_number = ? AND v.exit_time IS NULL
            ORDER BY v.ticket_id DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plate.toUpperCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String plateNo = rs.getString("plate_number");
                String vehicleType = rs.getString("vehicle_type");
                LocalDateTime entry = LocalDateTime.parse(rs.getString("entry_time"));
                String spotId = rs.getString("spot_id");
                SpotType spotType = SpotType.valueOf(rs.getString("type"));
                boolean oku  = rs.getInt("is_oku_cardholder") == 1;

                return new ActiveParkingRecord(plateNo, vehicleType, entry, spotId, spotType, oku);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateExitAndFee(String plate, LocalDateTime exitTime, double feeCollected) {
        String sql = """
            UPDATE vehicle
            SET exit_time = ?, fee_collected = ?
            WHERE plate_number = ? AND exit_time IS NULL
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, exitTime.toString());
            ps.setDouble(2, feeCollected);
            ps.setString(3, plate.toUpperCase());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
