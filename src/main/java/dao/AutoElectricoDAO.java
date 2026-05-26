package dao;

import model.AutoElectrico;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class AutoElectricoDAO {

    public void guardar(AutoElectrico auto) throws ServiceException {
        String sql = "INSERT INTO auto_electrico (marca, modelo, anio, color, precio, numero_puertas, cap_pasajeros, tipo_carro, traccion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, auto.getMarca());
            ps.setString(2, auto.getModelo());
            ps.setInt(3, 2024);
            ps.setString(4, null);
            ps.setDouble(5, auto.getPrecioBase());
            ps.setInt(6, auto.getNumeroPuertas());
            ps.setInt(7, auto.getNumeroPasajeros());
            ps.setString(8, auto.getTipoCarga());
            ps.setString(9, auto.getTraccion());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Auto guardado: " + auto.getMarca() + " " + auto.getModelo());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar auto: " + e.getMessage(), e);
        }
    }

    public ArrayList<AutoElectrico> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM auto_electrico";
        ArrayList<AutoElectrico> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer autos: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM auto_electrico WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("AUTO_NO_ENCONTRADO", "No se encontró el auto");

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar auto: " + e.getMessage(), e);
        }
    }

    private AutoElectrico mapear(ResultSet rs) throws SQLException {
        return new AutoElectrico(
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                0.0,
                0.0,
                rs.getDouble("precio"),
                0,
                rs.getInt("numero_puertas"),
                rs.getInt("cap_pasajeros"),
                rs.getString("tipo_carro"),
                rs.getString("traccion")
        );
    }
}
