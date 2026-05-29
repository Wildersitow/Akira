package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL      = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USUARIO  = "AKIRA";
    private static final String PASSWORD = "akira123";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
