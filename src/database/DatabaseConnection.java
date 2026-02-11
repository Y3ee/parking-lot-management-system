package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // 1. Get the dynamic path to the project root
            String projectRoot = System.getProperty("user.dir");
            
            // 2. Build the full path to parking.db
            // This works on Windows (\) and Mac (/) automatically
            String dbPath = projectRoot + File.separator + "parking.db";
            
            String url = "jdbc:sqlite:" + dbPath;

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            
            // 3. Print the path so you can debug!
            System.out.println("Database connected at: " + dbPath);
            
            createTables();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS parking_spot (
                    spot_id TEXT PRIMARY KEY,
                    type TEXT,
                    status TEXT,
                    hourly_rate REAL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vehicle (
                    ticket_id INTEGER PRIMARY KEY AUTOINCREMENT,  -- UNIQUE ID for every visit
                    plate_number TEXT,                            -- Can repeat (Returning customer)
                    vehicle_type TEXT,
                    entry_time TEXT,
                    exit_time TEXT,
                    spot_id TEXT,
                    fee_collected REAL DEFAULT 0.0                -- To calculate Total Revenue later
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fine (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plate_number TEXT,
                    amount REAL,
                    paid INTEGER
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
