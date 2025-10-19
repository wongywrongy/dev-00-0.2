package cs151.application;

import java.nio.file.Paths;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StudentDatabase {
    private static final String DB_PATH =
            Paths.get(System.getProperty("user.dir"), "lib", "languages.db").toString();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;


    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement s = c.createStatement()) {


            s.execute("""
                CREATE TABLE IF NOT EXISTS students (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  full_name TEXT NOT NULL UNIQUE COLLATE NOCASE,
                  academic_status TEXT NOT NULL,
                  employed INTEGER NOT NULL,
                  job_details TEXT,
                  databases_known TEXT NOT NULL,
                  preferred_role TEXT NOT NULL,
                  whitelist INTEGER NOT NULL DEFAULT 0,
                  blacklist INTEGER NOT NULL DEFAULT 0,
                  created_at TEXT NOT NULL
                )
            """);


            s.execute("""
                CREATE TABLE IF NOT EXISTS student_languages (
                  student_id INTEGER NOT NULL,
                  language_name TEXT NOT NULL,
                  PRIMARY KEY (student_id, language_name),
                  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
                )
            """);




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean existsByName(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) return false;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps =
                     c.prepareStatement("SELECT 1 FROM students WHERE full_name = ? COLLATE NOCASE")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean addStudentProfile(String rawName,
                                            String academicStatus,
                                            boolean employed,
                                            String jobDetails,
                                            List<String> languages,
                                            List<String> databases,
                                            String preferredRole,
                                            boolean whitelist,
                                            boolean blacklist,
                                            String initialCommentIfAny) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) return false;
        if (whitelist && blacklist) return false;

        String now = Instant.now().toString();
        String dbCsv = String.join(",", databases);

        final String insertStudent = """
            INSERT INTO students
            (full_name, academic_status, employed, job_details, databases_known,
             preferred_role, whitelist, blacklist, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DriverManager.getConnection(URL)) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, academicStatus);
                ps.setInt(3, employed ? 1 : 0);
                ps.setString(4, employed ? (jobDetails == null ? "" : jobDetails.trim()) : "");
                ps.setString(5, dbCsv);
                ps.setString(6, preferredRole);
                ps.setInt(7, whitelist ? 1 : 0);
                ps.setInt(8, blacklist ? 1 : 0);
                ps.setString(9, now);
                ps.executeUpdate();


                int studentId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key for student");
                    studentId = keys.getInt(1);
                }


                try (PreparedStatement psLang = c.prepareStatement(
                        "INSERT INTO student_languages(student_id, language_name) VALUES (?, ?)")) {
                    for (String lang : languages) {
                        String L = lang == null ? "" : lang.trim();
                        if (!L.isEmpty()) {
                            psLang.setInt(1, studentId);
                            psLang.setString(2, L);
                            psLang.addBatch();
                        }
                    }
                    psLang.executeBatch();
                }

                if (initialCommentIfAny != null && !initialCommentIfAny.isBlank()) {
                    try (PreparedStatement psC = c.prepareStatement(
                            "INSERT INTO student_comments(student_id, content, created_at) VALUES (?, ?, ?)")) {
                        psC.setInt(1, studentId);
                        psC.setString(2, initialCommentIfAny.trim());
                        psC.setString(3, now);
                        psC.executeUpdate();
                    }
                }




                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                return false;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static String getLatestComment(Connection c, int studentId) throws SQLException {
        final String sql = """
            SELECT content
            FROM student_comments
            WHERE student_id = ?
            ORDER BY datetime(created_at) DESC
            LIMIT 1
        """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "";
            }
        }
    }


    public static List<StudentProfile> getAllProfilesSorted() {
        List<StudentProfile> out = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY LOWER(TRIM(full_name)) ASC";

        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String academic = rs.getString("academic_status");
                boolean employed = rs.getInt("employed") == 1;
                String job = rs.getString("job_details");
                String dbCsv = rs.getString("databases_known");
                String role = rs.getString("preferred_role");
                boolean whitelist = rs.getInt("whitelist") == 1;
                boolean blacklist = rs.getInt("blacklist") == 1;

                
                List<String> langs = new ArrayList<>();
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT language_name FROM student_languages WHERE student_id=? ORDER BY LOWER(language_name)")) {
                    ps.setInt(1, id);
                    try (ResultSet r2 = ps.executeQuery()) {
                        while (r2.next()) langs.add(r2.getString(1));
                    }
                }


                List<String> dbs = dbCsv == null || dbCsv.isBlank()
                        ? List.of()
                        : Arrays.stream(dbCsv.split("\\s*,\\s*")).toList();


                String latestComment = getLatestComment(c, id);

                out.add(new StudentProfile(
                        id, fullName, academic, employed, job, langs, dbs, role, whitelist, blacklist, latestComment
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
}