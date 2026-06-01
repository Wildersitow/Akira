package dao;

import model.EstadoVehiculo;
import model.PatinetaElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class PatinetaElectricaDAO {

    public void guardar(PatinetaElectrica patineta) throws ServiceException {
        String sql = "INSERT INTO patineta_electrica (marca, modelo, anio, color, precio_base, autonomia_km, capacidad_bateria , estado_id, velocidad_max_kmh, peso_plat_kg, plegable, carga_maxima_kg, imagen) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try  {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setString(1, patineta.getMarca());
            ps.setString(2, patineta.getModelo());
            ps.setInt(3, patineta.getAnio());
            ps.setString(4, patineta.getColor());
            ps.setDouble(5, patineta.getPrecioBase());
            ps.setDouble(6, patineta.getAutonomiaKm());
            ps.setDouble(7, patineta.getCapacidadBateria());
            ps.setInt(8, estadoToId(patineta.getEstado()));
            ps.setInt(9, patineta.getVelocidadMaximaKmH());
            ps.setDouble(10, 0.0);
            ps.setInt(11, patineta.isEsPlegable() ? 1 : 0);
            ps.setInt(12, patineta.getCargaMaximaKg());
            ps.setString(13, patineta.getImagen());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Patineta guardada: " + patineta.getMarca() + " " + patineta.getModelo());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar patineta: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
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
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("PATINETA_NO_ENCONTRADA", "No se encontró la patineta con id: " + id);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar patineta: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private PatinetaElectrica mapear(ResultSet rs) throws SQLException {
        PatinetaElectrica patineta = new PatinetaElectrica(
                rs.getInt("anio"),
                rs.getDouble("autonomia_km"),
                rs.getDouble("capacidad_bateria"),
                rs.getString("color"),
                idToEstado(rs.getInt("estado_id")),
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getDouble("precio_base"),
                rs.getInt("carga_maxima_kg"),
                rs.getInt("plegable") == 1,
                rs.getInt("velocidad_max_kmh")
        );
        patineta.setImagen(rs.getString("imagen"));
        return patineta;
    }

    private int estadoToId(EstadoVehiculo estado) {
        return switch (estado) {
            case DISPONIBLE    -> 1;
            case VENDIDO       -> 2;
            case ALQUILADO     -> 3;
            case MANTENIMIENTO -> 4;
        };
    }

    private EstadoVehiculo idToEstado(int id) {
        return switch (id) {
            case 2  -> EstadoVehiculo.VENDIDO;
            case 3  -> EstadoVehiculo.ALQUILADO;
            case 4  -> EstadoVehiculo.MANTENIMIENTO;
            default -> EstadoVehiculo.DISPONIBLE;
        };
    }

    public PatinetaElectrica obtenerPorId(long id) throws ServiceException {
        String sql = "SELECT * FROM patineta_electrica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);

            throw new ServiceException("PATINETA_NO_ENCONTRADA", "No se encontró la patineta con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar patineta: " + e.getMessage(), e);
        }
    }

    public void actualizarCampos(long id, Map<String, Object> campos) throws ServiceException {
        if (campos == null || campos.isEmpty())
            throw new ServiceException("SIN_CAMPOS", "No se especificaron campos a actualizar.");

        StringBuilder sql = new StringBuilder("UPDATE patineta_electrica SET ");
        List<Object> valores = new ArrayList<>();

        campos.forEach((campo, valor) -> {
            sql.append(campo).append(" = ?, ");
            valores.add(valor);
        });

        sql.delete(sql.length() - 2, sql.length());
        sql.append(" WHERE id = ?");
        valores.add(id);

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql.toString());
            con.setAutoCommit(false);

            for (int i = 0; i < valores.size(); i++)
                ps.setObject(i + 1, valores.get(i));

            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("PATINETA_NO_ENCONTRADA", "No se encontró la patineta con id: " + id);

            System.out.println("✓ Patineta actualizada, campos: " + campos.keySet());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar patineta: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
