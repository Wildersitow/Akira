package dao;

import model.PatinetaElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class PatinetaElectricaDAO {

    public void guardar(PatinetaElectrica patineta) throws ServiceException {
        String sql = "INSERT INTO patineta_electrica (marca, modelo, anio, precio, velocidad_max_kmh, peso_plat_kg, plegable, carga_maxima_kg) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, patineta.getMarca());
            ps.setString(2, patineta.getModelo());
            ps.setInt(3, 2024);
            ps.setDouble(4, patineta.getPrecioBase());
            ps.setInt(5, patineta.getVelocidadMaxima());
            ps.setDouble(6, patineta.getPesoDispositivoKg());
            ps.setInt(7, patineta.isEsPlegable() ? 1 : 0);
            ps.setDouble(8, patineta.getPesoMaximoUsuarioKg());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Patineta guardada: " + patineta.getMarca() + " " + patineta.getModelo());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar patineta: " + e.getMessage(), e);
        }
    }

    public ArrayList<PatinetaElectrica> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM patineta_electrica";
        ArrayList<PatinetaElectrica> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer patinetas: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM patineta_electrica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("PATINETA_NO_ENCONTRADA", "No se encontró la patineta");

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar patineta: " + e.getMessage(), e);
        }
    }

    private PatinetaElectrica mapear(ResultSet rs) throws SQLException {
        return new PatinetaElectrica(
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                0.0, 0.0,
                rs.getDouble("precio"),
                rs.getInt("velocidad_max_kmh"),
                rs.getInt("plegable") == 1,
                rs.getDouble("peso_plat_kg"),
                rs.getDouble("carga_maxima_kg")
        );
    }
}
