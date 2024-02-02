package entities;

public class BannedWordEntry {
    private final String serverId;
    private final String word;

    public BannedWordEntry(String serverId, String word) {
        this.serverId = serverId;
        this.word = word;
    }

    public String getServerId() {
        return serverId;
    }

    public String getWord() {
        return word;
    }
}
