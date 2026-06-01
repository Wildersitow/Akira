package dao;

import model.EstadoVehiculo;
import model.MotoElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class MotoElectricaDAO {

    public void guardar(MotoElectrica moto) throws ServiceException {
        String sql = "INSERT INTO moto_electrica (marca, modelo, anio, color, precio_base, autonomia_km, capacidad_bateria, estado_id, tipo_moto, peso_kg, velocidad_max_kmh, imagen) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try  {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setString(1, moto.getMarca());
            ps.setString(2, moto.getModelo());
            ps.setInt(3, moto.getAnio());
            ps.setString(4, moto.getColor());
            ps.setDouble(5, moto.getPrecioBase());
            ps.setDouble(6, moto.getAutonomiaKm());
            ps.setDouble(7, moto.getCapacidadBateria());
            ps.setInt(8, estadoToId(moto.getEstado()));
            ps.setString(9, moto.getTipoMoto());
            ps.setDouble(10, moto.getPesoKg());
            ps.setInt(11,    moto.getVelocidadMaximaKmH());
            ps.setString(12, moto.getImagen());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Moto guardada: " + moto.getMarca() + " " + moto.getModelo());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar moto: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
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
        Connection con = null;
        try  {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("MOTO_NO_ENCONTRADA", "No se encontró la moto con id: " + id);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar moto: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private MotoElectrica mapear(ResultSet rs) throws SQLException {
        MotoElectrica moto = new MotoElectrica(
                rs.getInt("anio"),
                rs.getDouble("autonomia_km"),
                rs.getDouble("capacidad_bateria"),
                rs.getString("color"),
                idToEstado(rs.getInt("estado_id")),
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getDouble("precio_base"),
                rs.getString("tipo_moto"),
                rs.getDouble("peso_kg"),
                rs.getInt("velocidad_max_kmh")
        );
        moto.setImagen(rs.getString("imagen"));
        return moto;
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

    public MotoElectrica obtenerPorId(long id) throws ServiceException {
        String sql = "SELECT * FROM moto_electrica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);

            throw new ServiceException("MOTO_NO_ENCONTRADA", "No se encontró la moto con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar moto: " + e.getMessage(), e);
        }
    }

    public void actualizarCampos(long id, Map<String, Object> campos) throws ServiceException {
        if (campos == null || campos.isEmpty())
            throw new ServiceException("SIN_CAMPOS", "No se especificaron campos a actualizar.");

        StringBuilder sql = new StringBuilder("UPDATE moto_electrica SET ");
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
                throw new ServiceException("MOTO_NO_ENCONTRADA", "No se encontró la moto con id: " + id);

            System.out.println("✓ Moto actualizada, campos: " + campos.keySet());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar moto: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}