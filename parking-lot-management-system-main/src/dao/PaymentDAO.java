package dao;

import database.DatabaseConnection;
import model.Receipt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class PaymentDAO {

    public void saveReceipt(Receipt receipt) {

        String sql = """
            INSERT INTO payment(
                plate_number, payment_time, payment_method,
                entry_time, exit_time, duration_hours,
                hourly_rate, parking_fee,
                unpaid_fines, new_fines,
                total_paid, remaining_balance
            )
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            var bill = receipt.getBill();

            ps.setString(1, bill.getPlate().toUpperCase());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setString(3, receipt.getPaymentMethod());

            ps.setString(4, bill.getEntryTime().toString());
            ps.setString(5, bill.getExitTime().toString());
            ps.setInt(6, (int) bill.getHours());

            ps.setDouble(7, bill.getHourlyRate());
            ps.setDouble(8, bill.getParkingFee());

            ps.setDouble(9, bill.getUnpaidFines());
            ps.setDouble(10, bill.getNewFines());

            ps.setDouble(11, receipt.getTotalPaid());
            ps.setDouble(12, receipt.getRemainingBalance());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
