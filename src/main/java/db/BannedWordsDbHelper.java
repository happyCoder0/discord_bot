package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BannedWordsDbHelper {

    public void insert(String serverId, String word) {
        String sql = "insert into banned_words(server_id, word) values(?, ?) on conflict do nothing";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, word);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (Exception ex) {
            System.out.println("insertion failed");
            System.out.println(ex.getClass().getName());
            System.out.println(ex.getMessage().toLowerCase(Locale.ROOT));
        }
    }

    public List<String> findBannedWordsByServerId(String serverId) {
        ArrayList<String> words = new ArrayList<>();
        String sql = "select word from banned words where server_id = ?";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String word = resultSet.getString(1);
                words.add(word);
            }

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return words;
    }

    public void deleteBannedWord(String serverId, String word) {
        String sql = "delete from banned_words where server_id = ? and word = ?";
        Connection connection = DbUtil.getDbConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverId);
            preparedStatement.setString(2, word);
            preparedStatement.executeUpdate(sql);

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
