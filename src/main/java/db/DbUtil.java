package db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbUtil {
    public static Connection getDbConnection() {
        Properties properties = new Properties();
        Connection connection = null;
        try {
            properties.load(Files.newInputStream(Paths.get("src/app.properties")));
            String dbUrl = properties.getProperty("db_url");
            connection = DriverManager.getConnection(dbUrl);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return connection;
    }
}
