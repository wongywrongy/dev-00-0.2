package cs151.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LanguageDatabase {
    private static final Path DB_DIR = Paths.get(System.getProperty("user.dir"), "lib");
    private static final String DB_FILE = "database.db";
    private static final String URL = "jdbc:sqlite:" + DB_DIR.resolve(DB_FILE);

    // Initialize database and create table if it doesn't exist
    static {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS languages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE COLLATE NOCASE)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a language to database
    public static void addLanguage(String name) {
        String sql = "INSERT OR IGNORE INTO languages (name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all languages sorted alphabetically
    public static List<String> getAllLanguages() {
        List<String> languages = new ArrayList<>();
        String sql = "SELECT name FROM languages ORDER BY name COLLATE NOCASE";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                languages.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages;
    }
}

