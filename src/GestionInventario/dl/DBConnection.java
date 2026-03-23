package GestionInventario.dl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/inventario_juegos?serverTimezone=UTC";
    private static final String USER = "kat";
    private static final String PASSWORD = "unBong";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}