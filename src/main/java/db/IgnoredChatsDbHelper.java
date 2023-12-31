package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// TODO
public class IgnoredChatsDbHelper {
    public void insert(String serverId, String chatId) {
        String sql = "insert into ignored_chats (server_id, chat_id) values (?, ?)";
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
        boolean result = false;
        String sql = "select case when count(*) > 0 then true else false end where server_id = ? and chat_id = ?";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serverId);
            statement.setString(2, chatId);
            ResultSet resultSet = statement.executeQuery();
            result = resultSet.getBoolean(1);
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        return result;
    }

    public void delete(String serverId, String chatId) {
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
}
