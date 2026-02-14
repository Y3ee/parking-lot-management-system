package dao;

import database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import model.FineReport;
import model.FineReport.OutstandingFine; 
import model.OccupancyReport;
import model.OccupancyReport.FloorOccupancy;
import model.OccupancyReport.TypeOccupancy;
import model.RevenueReport;
import model.SpotType;
import model.VehicleListReport;
import model.VehicleListReport.ParkedVehicle;


public class ReportDAO {
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();}

    //occupancy report
    public OccupancyReport getOccupancyReport() {
        try {
            int totalSpots = getTotalSpots();
            int occupiedSpots = getOccupiedSpots();
            Map<Integer, FloorOccupancy> floorOcc = getFloorOccupancy();
            Map<SpotType, TypeOccupancy> typeOcc = getTypeOccupancy();
            return new OccupancyReport(totalSpots, occupiedSpots, floorOcc, typeOcc);

        } catch (Exception e) { 
            e.printStackTrace();
            return null;
        }
    }
    
    private int getTotalSpots() throws SQLException {
        String sql = "SELECT COUNT(*) FROM parking_spot";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0; 
            }
    }

    private int getOccupiedSpots() throws SQLException {
        String sql = "SELECT COUNT(*) FROM parking_spot WHERE status = 'OCCUPIED'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Map<Integer, FloorOccupancy> getFloorOccupancy() throws SQLException {
        Map<Integer, FloorOccupancy> map = new HashMap<>();

        String sql = """
            SELECT
                CAST(SUBSTR(spot_id, 2, 1) AS INTEGER) as floor,
                COUNT(*) as total,
                SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) as occupied
            FROM parking_spot
            GROUP BY floor
            ORDER BY floor                    
        """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int floor = rs.getInt("floor");
                int total = rs.getInt("total");
                int occupied = rs.getInt("occupied");
                map.put(floor, new FloorOccupancy(floor, total, occupied));
             }
        }

        return map;
    }

    private Map<SpotType, TypeOccupancy> getTypeOccupancy() throws SQLException {
        Map<SpotType, TypeOccupancy> map = new HashMap<>();

        String sql = """
            SELECT
                type,
                COUNT(*) as total,
                SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) as occupied
            FROM parking_spot
            GROUP BY type
        """;
                
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SpotType type = SpotType.valueOf(rs.getString("type"));
                int total = rs.getInt("total");
                int occupied = rs.getInt("occupied");
                map.put(type, new TypeOccupancy(type, total, occupied));
            }
        }

        return map;
    }

    //revenue report
    public RevenueReport getRevenueReport() {
        try {
            // each revenue
            double parkingRevenue = getTotalParkingRevenue();
            double fineRevenue = getTotalFineRevenue();
            //transaction count
            int transactions = getTotalTransactions();
            //unique vehicles served
            int vehiclesServed = getTotalVehiclesServed();
            
            //time based
            double todayRevenue = getRevenueForPeriod("today");
            double weekRevenue = getRevenueForPeriod("week");
            double monthRevenue = getRevenueForPeriod("month");

            return new RevenueReport(
                parkingRevenue, fineRevenue, transactions, vehiclesServed,
                todayRevenue, weekRevenue, monthRevenue
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private double getTotalParkingRevenue() throws SQLException {
    String sql = "SELECT COALESCE(SUM(fee_collected), 0) FROM vehicle WHERE exit_time IS NOT NULL";
    try (PreparedStatement ps = getConnection().prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private double getTotalFineRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(unpaid_fines + new_fines), 0) FROM payment";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private int getTotalTransactions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM payment";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    private int getTotalVehiclesServed() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT plate_number) FROM vehicle";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    private double getRevenueForPeriod(String period) throws SQLException {
        String sql = switch (period) {
            case "today" -> """
                SELECT COALESCE(SUM(total_paid), 0) FROM payment 
                WHERE DATE(payment_time) = DATE('now')
            """;
            case "week" -> """
                SELECT COALESCE(SUM(total_paid), 0) FROM payment 
                WHERE DATE(payment_time) >= DATE('now', '-7 days')
            """;
            case "month" -> """
                SELECT COALESCE(SUM(total_paid), 0) FROM payment 
                WHERE DATE(payment_time) >= DATE('now', 'start of month')
            """;
            default -> "SELECT 0";
        };
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }


    //fine report
    public FineReport getFineReport() {
        try {
            double unpaid = getTotalUnpaidFines();
            double paid = getTotalPaidFines();
            int vehiclesWithFines = getVehicleCountWithFines();
            List<OutstandingFine> outstanding = getOutstandingFines();
            
            return new FineReport(unpaid, paid, vehiclesWithFines, outstanding);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private double getTotalUnpaidFines() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fine WHERE status = 'UNPAID'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
    
    private double getTotalPaidFines() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fine WHERE status = 'PAID'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
    
    private int getVehicleCountWithFines() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT plate_number) FROM fine WHERE status = 'UNPAID'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    private List<OutstandingFine> getOutstandingFines() throws SQLException {
        List<OutstandingFine> list = new ArrayList<>();
        
        String sql = """
            SELECT 
                plate_number,
                SUM(amount) as total_amount,
                COUNT(*) as fine_count
            FROM fine
            WHERE status = 'UNPAID'
            GROUP BY plate_number
            ORDER BY total_amount DESC
        """;
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new OutstandingFine(
                    rs.getString("plate_number"),
                    rs.getDouble("total_amount"),
                    rs.getInt("fine_count")
                ));
            }
        }
        
        return list;
    }


    //vehicle list report
    public VehicleListReport getVehicleListReport() {
        try {
            List<ParkedVehicle> vehicles = getCurrentlyParkedVehicles();
            return new VehicleListReport(vehicles);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private List<ParkedVehicle> getCurrentlyParkedVehicles() throws SQLException {
        List<ParkedVehicle> list = new ArrayList<>();
        
        String sql = """
            SELECT 
                v.plate_number,
                v.vehicle_type,
                v.spot_id,
                p.type,
                v.entry_time,
                COALESCE(v.is_oku_cardholder, 0) as is_oku_cardholder
            FROM vehicle v
            JOIN parking_spot p ON v.spot_id = p.spot_id
            WHERE v.exit_time IS NULL
            ORDER BY v.entry_time
        """;
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean oku = rs.getInt("is_oku_cardholder") == 1;

                list.add(new ParkedVehicle(
                        rs.getString("plate_number"),
                        rs.getString("vehicle_type"),
                        rs.getString("spot_id"),
                        SpotType.valueOf(rs.getString("type")),
                        LocalDateTime.parse(rs.getString("entry_time")),
                        oku
                ));
            }
        }
        
        return list;
    }
}