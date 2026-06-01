package dao;

import model.EstadoVehiculo;
import model.MotoElectrica;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class MotoElectricaDAO {

    public void guardar(MotoElectrica moto) throws ServiceException {
        String sql = "INSERT INTO moto_electrica (marca, modelo, anio, color, precio_base, autonomia_km, capacidad_bateria, potencia_motor_kw, estado_id, tipo_moto, peso_kg, altura_asiento_mm,imagen) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, moto.getMarca());
            ps.setString(2, moto.getModelo());
            ps.setInt(3, moto.getAnio());
            ps.setString(4, moto.getColor());
            ps.setDouble(5, moto.getPrecioBase());
            ps.setDouble(6, moto.getAutonomiaKm());
            ps.setDouble(7, moto.getCapacidadBateria());
            ps.setInt(8, moto.getPotenciaMotorKW());
            ps.setInt(9, estadoToId(moto.getEstado()));
            ps.setString(10, moto.getTipoMoto());
            ps.setDouble(11, moto.getPesoKg());
            ps.setInt(12, moto.getAlturaAsientoMm());
            ps.setString(13, moto.getImagen());
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
                throw new ServiceException("MOTO_NO_ENCONTRADA", "No se encontró la moto con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar moto: " + e.getMessage(), e);
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
                rs.getInt("potencia_motor_kw"),
                0,                            // velocidadMaxima — no está en la tabla
                rs.getInt("altura_asiento_mm"),
                rs.getString("tipo_moto"),
                rs.getDouble("peso_kg")
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
}