package dao;

import model.AutoElectrico;
import model.EstadoVehiculo;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class AutoElectricoDAO {

    public void guardar(AutoElectrico auto) throws ServiceException {
        String sql = "INSERT INTO auto_electrico (marca, modelo, anio, color, precio_base, estado_id, numero_puertas, tipo_carro, cap_pasajeros, traccion, autonomia_km, capacidad_bateria, velocidad_maxima, imagen) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setString(1,  auto.getMarca());
            ps.setString(2,  auto.getModelo());
            ps.setInt(3,     auto.getAnio());
            ps.setString(4,  auto.getColor());
            ps.setDouble(5,  auto.getPrecioBase());
            ps.setInt(6,     estadoToId(auto.getEstado()));
            ps.setInt(7,     auto.getNumeroPuertas());
            ps.setString(8,  auto.getTipoCarro());
            ps.setInt(9,     auto.getNumeroPasajeros());
            ps.setString(10, auto.getTraccion());
            ps.setDouble(11, auto.getAutonomiaKm());
            ps.setDouble(12, auto.getCapacidadBateria());
            ps.setInt(13,    auto.getVelocidadMaxima());
            ps.setString(14, auto.getImagen());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Auto guardado: " + auto.getMarca() + " " + auto.getModelo());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar auto: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
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
        Connection con = null;
        try  {
            con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            con.setAutoCommit(false);
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("AUTO_NO_ENCONTRADO", "No se encontró el auto con id: " + id);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar auto: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private AutoElectrico mapear(ResultSet rs) throws SQLException {
        AutoElectrico auto = new AutoElectrico(
                rs.getInt("anio"),
                rs.getDouble("autonomia_km"),
                rs.getDouble("capacidad_bateria"),
                rs.getString("color"),
                idToEstado(rs.getInt("estado_id")),
                String.valueOf(rs.getLong("id")),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getDouble("precio_base"),
                rs.getInt("velocidad_maxima"),
                rs.getInt("cap_pasajeros"),
                rs.getInt("numero_puertas"),
                rs.getString("tipo_carro"),
                rs.getString("traccion")
        );
        auto.setImagen(rs.getString("imagen"));
        return auto;
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
}
