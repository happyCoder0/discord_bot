package entities;

public class UserEntry {
    private final String serverId;
    private final String userId;
    private final int warnings;

    public UserEntry(String serverId, String userId, int warnings) {
        this.serverId = serverId;
        this.userId = userId;
        this.warnings = warnings;
    }

    public String getServerId() {
        return serverId;
    }

    public String getUserId() {
        return userId;
    }

    public int getWarnings() {
        return warnings;
    }
}
