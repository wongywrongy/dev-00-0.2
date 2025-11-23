package cs151.application;

public class Comment {
    private final String date;
    private final String content;

    public Comment(String date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    // For display in table - truncate if too long
    public String getExcerpt() {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 97) + "...";
    }
}

