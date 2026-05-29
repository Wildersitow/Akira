package dao;

import model.BicicletaElectrica;
import model.EstadoVehiculo;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class BicicletaElectricaDAO {

    public void guardar(BicicletaElectrica bici) throws ServiceException {
        String sql = "INSERT INTO bicicleta_electrica (marca, modelo, anio, color, precio_base, autonomia_km, capacidad_bateria, potencia_motor_kw, estado_id, tipo_asistencia, velocidad_max_kmh, num_cambios, material_marco) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, bici.getMarca());
            ps.setString(2, bici.getModelo());
            ps.setInt(3, bici.getAnio());
            ps.setString(4, bici.getColor());
            ps.setDouble(5, bici.getPrecioBase());
            ps.setDouble(6, bici.getAutonomiaKm());
            ps.setDouble(7, bici.getCapacidadBateria());
            ps.setInt(8, bici.getPotenciaMotorKW());
            ps.setInt(9, estadoToId(bici.getEstado()));
            ps.setString(10, bici.getTipoAsistencia());
            ps.setInt(11, bici.getVelocidadMaximaKmH());
            ps.setInt(12, bici.getNumeroMarchas());
            ps.setString(13, bici.getMaterialMarco());
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
                throw new ServiceException("BICI_NO_ENCONTRADA", "No se encontró la bicicleta con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar bicicleta: " + e.getMessage(), e);
        }
    }

    private BicicletaElectrica mapear(ResultSet rs) throws SQLException {
        return new BicicletaElectrica(
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
                0,                              // velocidadMaxima heredada — no está en la tabla
                rs.getString("material_marco"),
                rs.getInt("velocidad_max_kmh"),
                rs.getString("tipo_asistencia"),
                rs.getInt("num_cambios")
        );
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