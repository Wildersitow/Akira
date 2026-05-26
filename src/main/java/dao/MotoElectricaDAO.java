package dao;

import model.MotoElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class MotoElectricaDAO {

    public void guardar(MotoElectrica moto) throws ServiceException {
        String sql = "INSERT INTO moto_electrica (marca, modelo, anio, precio, tipo_moto) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, moto.getMarca());
            ps.setString(2, moto.getModelo());
            ps.setInt(3, 2024);
            ps.setDouble(4, moto.getPrecioBase());
            ps.setString(5, moto.getTipoMoto());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Moto guardada: " + moto.getMarca() + " " + moto.getModelo());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar moto: " + e.getMessage(), e);
        }
    }

    public ArrayList<MotoElectrica> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM moto_electrica";
        ArrayList<MotoElectrica> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer motos: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM moto_electrica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("MOTO_NO_ENCONTRADA", "No se encontró la moto");

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar moto: " + e.getMessage(), e);
        }
    }

    private MotoElectrica mapear(ResultSet rs) throws SQLException {
        return new MotoElectrica(
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                0.0, 0.0,
                rs.getDouble("precio"),
                0, 0,
                rs.getString("tipo_moto"),
                null
        );
    }
}
