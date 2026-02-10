package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:sqlite:parking.db";

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database connected & tables ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
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
                    plate_number TEXT PRIMARY KEY,
                    vehicle_type TEXT,
                    entry_time TEXT,
                    exit_time TEXT,
                    spot_id TEXT
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
