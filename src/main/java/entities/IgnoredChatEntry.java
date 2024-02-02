package entities;

public class IgnoredChatEntry {
    private final String serverId;
    private final String chatId;

    public IgnoredChatEntry(String serverId, String chatId) {
        this.serverId = serverId;
        this.chatId = chatId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getChatId() {
        return chatId;
    }
}
