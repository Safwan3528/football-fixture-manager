package com.footballfixture;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static String SQLITE_URL = "jdbc:sqlite:football_fixture.db";
    private static String MYSQL_URL = "jdbc:mysql://localhost:3306/";
    private static String DATABASE_NAME = "football_fixture";
    private static String MYSQL_FULL_URL = MYSQL_URL + DATABASE_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=30000";
    private static String USER = "root";
    private static String PASSWORD = "";
    private static boolean useMySql = false;
    private static boolean bypassConnection = false;
    private static List<Connection> activeConnections = new ArrayList<>();

    public static void initializeDatabase() throws SQLException {
        if (bypassConnection) {
            System.out.println("Database connection bypassed");
            return;
        }

        if (useMySql) {
            initializeMySql();
        } else {
            initializeSqlite();
        }
    }

    private static void initializeMySql() throws SQLException {
        try {
            try (Connection conn = DriverManager.getConnection(MYSQL_URL, USER, PASSWORD)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
                }
                System.out.println("MySQL database checked/created successfully.");
            }
            
            try (Connection conn = DriverManager.getConnection(MYSQL_FULL_URL, USER, PASSWORD)) {
                System.out.println("Connected to MySQL successfully.");
                createTables(conn, true);
            }
        } catch (SQLException e) {
            System.err.println("MySQL initialization failed: " + e.getMessage());
            throw e;
        }
    }

    private static void initializeSqlite() throws SQLException {
        try {
            // SQLite database will be created automatically if it doesn't exist
            try (Connection conn = DriverManager.getConnection(SQLITE_URL)) {
                System.out.println("Connected to SQLite successfully.");
                createTables(conn, false);
            }
        } catch (SQLException e) {
            System.err.println("SQLite initialization failed: " + e.getMessage());
            throw e;
        }
    }

    private static void createTables(Connection conn, boolean isMySQL) throws SQLException {
        String autoIncrement = isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        String foreignKeyChecks = isMySQL ? "SET FOREIGN_KEY_CHECKS=0;" : "PRAGMA foreign_keys=OFF;";
        String enableForeignKeys = isMySQL ? "SET FOREIGN_KEY_CHECKS=1;" : "PRAGMA foreign_keys=ON;";
        String textType = isMySQL ? "VARCHAR(100)" : "TEXT";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(foreignKeyChecks);

            // Create teams table
            stmt.execute("CREATE TABLE IF NOT EXISTS teams (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "name " + textType + " NOT NULL," +
                    "country " + textType + " NOT NULL)");

            // Create stadiums table
            stmt.execute("CREATE TABLE IF NOT EXISTS stadiums (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "name " + textType + " NOT NULL," +
                    "city " + textType + " NOT NULL," +
                    "capacity INTEGER NOT NULL)");

            // Create players table
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "name " + textType + " NOT NULL," +
                    "age INTEGER NOT NULL," +
                    "position " + textType + " NOT NULL," +
                    "team_id INTEGER," +
                    "squad_no INTEGER NOT NULL," +
                    "goal_score INTEGER NOT NULL DEFAULT 0," +
                    "yellow_card INTEGER NOT NULL DEFAULT 0," +
                    "red_card INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY (team_id) REFERENCES teams(id))");

            // Create coaches table
            stmt.execute("CREATE TABLE IF NOT EXISTS coaches (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "name " + textType + " NOT NULL," +
                    "team_id INTEGER," +
                    "FOREIGN KEY (team_id) REFERENCES teams(id))");

            // Create matches table
            stmt.execute("CREATE TABLE IF NOT EXISTS matches (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "home_team_id INTEGER," +
                    "away_team_id INTEGER," +
                    "date_time DATETIME NOT NULL," +
                    "stadium_id INTEGER," +
                    "FOREIGN KEY (home_team_id) REFERENCES teams(id)," +
                    "FOREIGN KEY (away_team_id) REFERENCES teams(id)," +
                    "FOREIGN KEY (stadium_id) REFERENCES stadiums(id))");

            // Create match_results table
            stmt.execute("CREATE TABLE IF NOT EXISTS match_results (" +
                    "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                    "match_id INTEGER," +
                    "home_team_score INTEGER NOT NULL," +
                    "away_team_score INTEGER NOT NULL," +
                    "FOREIGN KEY (match_id) REFERENCES matches(id))");

            stmt.execute(enableForeignKeys);
            System.out.println("Tables created successfully.");
        }
    }

    public static void setConnectionDetails(String url, String user, String password, boolean useMySQL) {
        if (useMySQL) {
            MYSQL_URL = url;
            USER = user;
            PASSWORD = password;
            MYSQL_FULL_URL = MYSQL_URL + DATABASE_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=30000";
        }
        useMySql = useMySQL;
    }

    public static void setBypassConnection(boolean bypass) {
        bypassConnection = bypass;
    }

    public static Connection getConnection() throws SQLException {
        if (bypassConnection) {
            System.out.println("Database connection bypassed");
            return null;
        }
        Connection conn = useMySql ? 
            DriverManager.getConnection(MYSQL_FULL_URL, USER, PASSWORD) :
            DriverManager.getConnection(SQLITE_URL);
        activeConnections.add(conn);
        return conn;
    }

    public static String getURL() {
        return useMySql ? MYSQL_URL : SQLITE_URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static boolean isUseMySql() {
        return useMySql;
    }

    public static boolean isBypassConnection() {
        return bypassConnection;
    }

    public static void closeAllConnections() {
        for (Connection conn : activeConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        activeConnections.clear();
    }

    public static void deleteDatabase() {
        closeAllConnections(); // Tutup semua connection terlebih dahulu
        
        if (useMySql) {
            try (Connection conn = DriverManager.getConnection(MYSQL_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP DATABASE IF EXISTS " + DATABASE_NAME);
                System.out.println("Database deleted successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to delete MySQL database: " + e.getMessage());
            }
        } else {
            // Untuk SQLite, clear file database
            File dbFile = new File("football_fixture.db");
            if (dbFile.exists()) {
                if (dbFile.delete()) {
                    System.out.println("SQLite database file deleted successfully.");
                } else {
                    System.err.println("Failed to delete SQLite database file.");
                }
            } else {
                System.out.println("SQLite database file does not exist.");
            }
        }
    }
}
