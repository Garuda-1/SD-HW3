package ru.akirakozov.sd.refactoring.database;

import ru.akirakozov.sd.refactoring.utils.ThrowingConsumer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtils {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DATABASE_TABLE = "products";

    public static void executeUpdate(String sql) {
        try (Connection c = getConnection()) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeQuery(String sql, ThrowingConsumer<ResultSet> resultSetConsumer) {
        try (Connection c = getConnection()) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSetConsumer.accept(resultSet);
            resultSet.close();
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureProductTableExists() {
        executeUpdate(
                "CREATE TABLE IF NOT EXISTS Product (" +
                        "    ID BIGSERIAL PRIMARY KEY," +
                        "    name TEXT NOT NULL, " +
                        "    price INT NOT NULL)"
        );
    }

    private static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        return DriverManager.getConnection(DATABASE_URL + DATABASE_TABLE, properties);
    }
}
