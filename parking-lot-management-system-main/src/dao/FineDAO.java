package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FineDAO {

    // Sum all UNPAID fines
    public double getUnpaidTotal(String plate) {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM fine WHERE plate_number=? AND status='UNPAID'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plate.toUpperCase());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("total") : 0.0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Insert NEW fine (always UNPAID)
    public void addFine(String plate, double amount) {
        if (amount <= 0) return;

        String sql = "INSERT INTO fine(plate_number, amount, status) VALUES(?,?, 'UNPAID')";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plate.toUpperCase());
            ps.setDouble(2, amount);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mark ALL unpaid fines as PAID
    public void markAllPaid(String plate) {
        String sql = "UPDATE fine SET status='PAID' WHERE plate_number=? AND status='UNPAID'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plate.toUpperCase());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
