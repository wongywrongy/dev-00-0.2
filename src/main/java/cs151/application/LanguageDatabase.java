package cs151.application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LanguageDatabase {
    private static final String DB_PATH = "lib/languages.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

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

