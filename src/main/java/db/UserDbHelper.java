package db;

import entities.UserEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UserDbHelper {
    private static UserDbHelper instance;
    private final List<UserEntry> cache;

    private UserDbHelper() {
        cache = getAllRows();
    }

    public synchronized static UserDbHelper getInstance() {
        if (instance == null)
            instance = new UserDbHelper();

        return instance;
    }

    public List<UserEntry> getUsersByServerId(String serverId) {
        return cache.stream()
                .filter(u -> u.getServerId().equals(serverId))
                .collect(Collectors.toList());
    }

    public void addUser(String serverId, String userId) {
        if (!exists(serverId, userId))
            cache.add(new UserEntry(serverId, userId, 0));
        else return;

        String sql = "insert into users(server_id, user_id, warnings) values(?, ?, ?) on conflict do nothing";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, userId);
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException ex) {
            System.out.println("insertion failed");
            System.out.println(ex.getClass().getName());
            System.out.println(ex.getMessage().toLowerCase(Locale.ROOT));
        }
    }

    public void updateWarnings(String serverId, String userId, int newWarnings) {
        if (exists(serverId, userId)) {
            UserEntry oldEntry = cache.stream()
                    .filter(u -> u.getServerId().equals(serverId) && u.getUserId().equals(userId))
                    .findAny()
                    .orElseThrow(IllegalStateException::new);

            cache.remove(oldEntry);
            cache.add(new UserEntry(serverId, userId, newWarnings));
        }

        String sql = "update users set warnings = ? where server_id = ? and user_id = ?";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, newWarnings);
            preparedStatement.setString(2, serverId);
            preparedStatement.setString(3, userId);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deleteUser(String serverId, String userId) {
        if (!exists(serverId, userId)) return;

        cache.removeIf(u -> exists(u.getServerId(), u.getUserId()));

        String sql = "delete from users where server_id = ? and user_id = ?";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, userId);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int getWarnings(String serverId, String userId) {
        return cache.stream()
                .filter(u -> u.getServerId().equals(serverId)
                        && u.getUserId().equals(userId))
                .map(UserEntry::getWarnings)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public List<UserEntry> getUsers() {
        return cache;
    }

    private boolean exists(String serverId, String userId) {
        return cache.stream()
                .anyMatch(u -> u.getServerId().equals(serverId)
                        && u.getUserId().equals(userId));
    }

    private List<UserEntry> getAllRows() {
        List<UserEntry> rows = new ArrayList<>();
        String sql = "select server_id, user_id, warnings from users";
        Connection connection = DbUtil.getDbConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String serverId = resultSet.getString(1);
                String userId = resultSet.getString(2);
                int warnings = resultSet.getInt(3);
                rows.add(new UserEntry(serverId, userId, warnings));
            }

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return rows;
    }
}
