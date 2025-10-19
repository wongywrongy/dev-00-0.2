package cs151.application;

import java.util.List;

public class StudentProfile {
    private final int id;
    private final String fullName;
    private final String academicStatus;
    private final boolean employed;
    private final String jobDetails;
    private final List<String> languages;
    private final List<String> databases;
    private final String preferredRole;
    private final boolean whitelist;
    private final boolean blacklist;
    private final String comment;

    public StudentProfile(int id, String fullName, String academicStatus, boolean employed,
                          String jobDetails, List<String> languages, List<String> databases,
                          String preferredRole, boolean whitelist, boolean blacklist,
                          String latestComment) {
        this.id = id;
        this.fullName = fullName;
        this.academicStatus = academicStatus;
        this.employed = employed;
        this.jobDetails = jobDetails;
        this.languages = languages;
        this.databases = databases;
        this.preferredRole = preferredRole;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.comment = latestComment == null ? "" : latestComment;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getAcademicStatus() { return academicStatus; }
    public boolean isEmployed() { return employed; }
    public String getJobDetails() { return jobDetails; }
    public List<String> getLanguages() { return languages; }
    public List<String> getDatabases() { return databases; }
    public String getPreferredRole() { return preferredRole; }
    public boolean isWhitelist() { return whitelist; }
    public boolean isBlacklist() { return blacklist; }
    public String getLatestComment() { return comment; }

    public String getLanguagesCsv() { return String.join(", ", languages); }
    public String getDatabasesCsv() { return String.join(", ", databases); }
    public String getFlagsText() {
        if (whitelist) return "Whitelist";
        if (blacklist) return "Blacklist";
        return "";
    }
}
