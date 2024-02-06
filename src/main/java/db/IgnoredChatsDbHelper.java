package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class IgnoredChatsDbHelper {
    private static IgnoredChatsDbHelper instance;
    private final Map<String, List<String>> cache = new HashMap<>();

    private IgnoredChatsDbHelper() {
        List<Map.Entry<String, String>> allRows = getAllRows();

        List<String> ids = allRows
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        ids.forEach(serverId -> {
                cache.put(serverId, new ArrayList<>());

            List<String> chatIds = allRows.stream()
                    .filter(pair -> pair.getKey().equals(serverId))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            cache.get(serverId).addAll(chatIds);
        });
    }

    public static synchronized IgnoredChatsDbHelper getInstance() {
        if (instance == null) instance = new IgnoredChatsDbHelper();

        return instance;
    }

    public void insert(String serverId, String chatId) {
        cache.putIfAbsent(serverId, new ArrayList<>());
        List<String> chats = cache.get(serverId);
        if (!chats.contains(chatId)) chats.add(chatId);
        else return;

        String sql = "insert into ignored_chats (server_id, chat_id) values (?, ?) on conflict do nothing";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serverId);
            statement.setString(2, chatId);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean exists(String serverId, String chatId) {
        if (cache.get(serverId) == null) return false;

        return cache.get(serverId).contains(chatId);
    }

    public void delete(String serverId, String chatId) {
        if (cache.get(serverId) == null) return;
        if (!cache.get(serverId).contains(chatId)) return;

        cache.get(serverId).remove(chatId);

        String sql = "delete from ignored_chats where server_id = ? and chat_id = ?";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serverId);
            statement.setString(2, chatId);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public List<String> findIgnoredChatsByServerId(String serverId) {
        List<String> chatIds = cache.get(serverId);

        if (chatIds == null) return Collections.emptyList();
        return chatIds;
    }

    public void registerServer(String serverId) {
        cache.putIfAbsent(serverId, new ArrayList<>());
    }

    private List<Map.Entry<String, String>> getAllRows() {
        List<Map.Entry<String, String>> rows = new ArrayList<>();
        String sql = "select server_id, chat_id from ignored_chats";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String serverId = resultSet.getString(1);
                String chatId = resultSet.getString(2);
                rows.add(new AbstractMap.SimpleEntry<>(serverId, chatId));
            }

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return rows;
    }
}
