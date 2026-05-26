package dao;

import model.BicicletaElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class BicicletaElectricaDAO {

    public void guardar(BicicletaElectrica bici) throws ServiceException {
        String sql = "INSERT INTO bicicleta_electrica (marca, modelo, anio, precio, tipo_asistencia, num_cambios) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, bici.getMarca());
            ps.setString(2, bici.getModelo());
            ps.setInt(3, 2024);
            ps.setDouble(4, bici.getPrecioBase());
            ps.setString(5, bici.getTipoBicicleta());
            ps.setInt(6, bici.getNumeroMarchas());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Bicicleta guardada: " + bici.getMarca() + " " + bici.getModelo());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar bicicleta: " + e.getMessage(), e);
        }
    }

    public ArrayList<BicicletaElectrica> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM bicicleta_electrica";
        ArrayList<BicicletaElectrica> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer bicicletas: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM bicicleta_electrica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("BICI_NO_ENCONTRADA", "No se encontró la bicicleta");

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar bicicleta: " + e.getMessage(), e);
        }
    }

    private BicicletaElectrica mapear(ResultSet rs) throws SQLException {
        return new BicicletaElectrica(
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                0.0, 0.0,
                rs.getDouble("precio"),
                0,
                rs.getInt("num_cambios"),
                false,
                rs.getString("tipo_asistencia")
        );
    }
}