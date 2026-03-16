package GestionInventario.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/proyectoestructuradatos?serverTimezone=UTC";
    private static final String USER = "Erick";
    private static final String PASSWORD = "311224";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}