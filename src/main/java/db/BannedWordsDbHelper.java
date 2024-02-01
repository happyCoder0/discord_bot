package db;

import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class BannedWordsDbHelper {

    private static BannedWordsDbHelper instance;
    private final Map<String, List<String>> cache = new HashMap<>();

    private BannedWordsDbHelper() {
        System.out.println("Created banned words helper");
        List<Map.Entry<String, String>> allRows = getAllRows();

        List<String> ids = allRows.stream()
                        .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

        ids.forEach(id -> {
            cache.put(id, new ArrayList<>());

            List<String> words = allRows.stream()
                    .filter(pair -> pair.getKey().equals(id))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            cache.get(id).addAll(words);
        });
    }

    public static synchronized BannedWordsDbHelper getInstance() {
        if (instance == null) instance = new BannedWordsDbHelper();

        return instance;
    }

    public void insert(String serverId, String word) throws PSQLException {
        cache.putIfAbsent(serverId, new ArrayList<>());

        List<String> words = cache.get(serverId);
        if (!words.contains(word)) words.add(word);
        else return;

        String sql = "insert into banned_words(server_id, word) values(?, ?) on conflict do nothing";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, word);
            preparedStatement.executeUpdate();

            connection.close();
        }  catch (SQLException ex) {
            System.out.println("insertion failed");
            System.out.println(ex.getClass().getName());
            System.out.println(ex.getMessage().toLowerCase(Locale.ROOT));
        }
    }

    public List<String> findBannedWordsByServerId(String serverId) {
        return cache.get(serverId);
    }

    public void deleteBannedWord(String serverId, String word) {
        String sql = "delete from banned_words where server_id = ? and word = ?";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, word);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        cache.get(serverId).remove(word);
    }

    private List<Map.Entry<String, String>> getAllRows() {
        List<Map.Entry<String, String>> rows = new ArrayList<>();
        String sql = "select word, server_id from banned_words";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String word = resultSet.getString(1);
                String serverId = resultSet.getString(2);
                rows.add(new AbstractMap.SimpleEntry<>(serverId, word));
            }

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return rows;
    }
}
