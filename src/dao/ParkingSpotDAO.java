package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ParkingSpot;
import model.SpotType;

public class ParkingSpotDAO {

    // Method 1: Find Available Spot
    public ParkingSpot findAvailableSpot(SpotType type) {
        String sql = "SELECT * FROM parking_spot WHERE type = ? AND status = 'AVAILABLE' LIMIT 1";
        
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.toString()); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpot(
                        rs.getString("spot_id"),
                        SpotType.valueOf(rs.getString("type"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    // Method 2: Mark Occupied
    public void markOccupied(String spotId) {
        String sql = "UPDATE parking_spot SET status = 'OCCUPIED' WHERE spot_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method 3: Get ALL spots + Current Vehicle (For Admin Panel)
    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT p.*, v.plate_number FROM parking_spot p LEFT JOIN vehicle v ON p.spot_id = v.spot_id AND v.exit_time IS NULL";
        
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ParkingSpot spot = new ParkingSpot(
                    rs.getString("spot_id"),
                    SpotType.valueOf(rs.getString("type"))
                );
                String status = rs.getString("status");
                String plate = rs.getString("plate_number");

                if ("OCCUPIED".equalsIgnoreCase(status)) {
                    spot.occupy(plate != null ? plate : "Unknown");
                }
                spots.add(spot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spots;
    }

    public void initializeParkingLot() {
        if (getSpotCount() > 0) {
            System.out.println("✅ Parking Lot already initialized.");
            return;
        }

        System.out.println("⚠️ Database empty. Initializing custom spot layout...");

        String sql = "INSERT INTO parking_spot (spot_id, type, status, hourly_rate) VALUES (?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); 

            // 5 FLOORS
            for (int floor = 1; floor <= 5; floor++) {
                
                // 18 SPOTS per Floor
                for (int s = 1; s <= 18; s++) {
                    
                    // ID Format: F1-S1, F1-S2 ... F1-S18
                    String spotId = "F" + floor + "-S" + s;
                    
                    SpotType type = SpotType.REGULAR; // Default (RM 5.0)
                    
                    // --- YOUR SPECIFIC RULES ---
                    if (s >= 1 && s <= 2) {
                        type = SpotType.HANDICAPPED; // S1-S2
                    } 
                    else if (s >= 3 && s <= 4) {
                        type = SpotType.RESERVED;    // S3-S4
                    } 
                    else if ((s >= 5 && s <= 6) || (s >= 11 && s <= 12)) {
                        type = SpotType.COMPACT;     // S5-S6 and S11-S12
                    } 
                    else {
                        type = SpotType.REGULAR;     // All others (S7-S10, S13-S18)
                    }

                    pstmt.setString(1, spotId);
                    pstmt.setString(2, type.toString());
                    pstmt.setString(3, "AVAILABLE");
                    pstmt.setDouble(4, type.getRate()); 
                    
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            
            System.out.println("🎉 SUCCESS: Created spots 1-18 with your specific types.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to count spots
    private int getSpotCount() {
        String sql = "SELECT COUNT(*) FROM parking_spot";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}