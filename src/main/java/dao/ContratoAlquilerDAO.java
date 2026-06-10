package dao;

import model.*;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContratoAlquilerDAO {

    private final ClienteDAO  clienteDAO  = new ClienteDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public void guardar(ContratoAlquiler contrato, String tipoVehiculo) throws ServiceException {
        String sql = "INSERT INTO akira.contrato_alquiler " +
                "(cliente_id, vehiculo_id, tipo_vehiculo, empleado_id, precio_final, " +
                "forma_de_pago, fecha_inicio, fecha_fin, dias_alquilados, periodo, estado_contrato) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1,    contrato.getCliente().getId());
            ps.setLong(2,    Long.parseLong(contrato.getVehiculoElectrico().getId()));
            ps.setString(3,  tipoVehiculo.toUpperCase());
            ps.setLong(4,    contrato.getEmpleado().getId());
            ps.setDouble(5,  contrato.getPrecioFinal());
            ps.setString(6,  contrato.getFormaDePago());
            ps.setDate(7,    Date.valueOf(contrato.getFechaInicio()));
            ps.setDate(8,    Date.valueOf(contrato.getFechaFin()));
            ps.setInt(9,     contrato.getDiasAlquilados());
            ps.setString(10, contrato.getPeriodo());
            ps.setString(11, contrato.getEstadoContrato());

            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Contrato alquiler guardado, cliente id: "
                    + contrato.getCliente().getId());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO",
                    "Error al guardar contrato alquiler: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public ArrayList<ContratoAlquiler> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM akira.contrato_alquiler";
        ArrayList<ContratoAlquiler> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al leer contratos alquiler: " + e.getMessage(), e);
        }
    }

    public ContratoAlquiler obtenerPorId(long id) throws ServiceException {
        String sql = "SELECT * FROM akira.contrato_alquiler WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);

            throw new ServiceException("ALQUILER_NO_ENCONTRADO",
                    "No se encontró el contrato alquiler con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar contrato alquiler: " + e.getMessage(), e);
        }
    }

    public ArrayList<ContratoAlquiler> obtenerPorCliente(long clienteId) throws ServiceException {
        String sql = "SELECT * FROM akira.contrato_alquiler WHERE cliente_id = ?";
        ArrayList<ContratoAlquiler> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar alquileres del cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM akira.contrato_alquiler WHERE id = ?";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("ALQUILER_NO_ENCONTRADO",
                        "No se encontró el contrato alquiler con id: " + id);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION",
                    "Error al eliminar contrato alquiler: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void actualizarCampos(long id, Map<String, Object> campos) throws ServiceException {
        if (campos == null || campos.isEmpty())
            throw new ServiceException("SIN_CAMPOS", "No se especificaron campos a actualizar.");

        StringBuilder sql = new StringBuilder("UPDATE akira.contrato_alquiler SET ");
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
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < valores.size(); i++)
                ps.setObject(i + 1, valores.get(i));

            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("ALQUILER_NO_ENCONTRADO",
                        "No se encontró el contrato alquiler con id: " + id);

            System.out.println("✓ Contrato alquiler actualizado, campos: " + campos.keySet());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ACTUALIZACION",
                    "Error al actualizar contrato alquiler: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private VehiculoElectrico obtenerVehiculo(long vehiculoId, String tipoVehiculo) throws SQLException {
        try {
            return switch (tipoVehiculo) {
                case "AUTO"      -> new AutoElectricoDAO().obtenerPorId(vehiculoId);
                case "MOTO"      -> new MotoElectricaDAO().obtenerPorId(vehiculoId);
                case "BICICLETA" -> new BicicletaElectricaDAO().obtenerPorId(vehiculoId);
                case "PATINETA"  -> new PatinetaElectricaDAO().obtenerPorId(vehiculoId);
                default -> throw new SQLException("Tipo de vehículo desconocido: " + tipoVehiculo);
            };
        } catch (ServiceException e) {
            throw new SQLException("Error al obtener vehículo: " + e.getMessage());
        }
    }

    private ContratoAlquiler mapear(ResultSet rs) throws SQLException {
        try {
            Cliente  cliente  = clienteDAO.obtenerPorId(rs.getLong("cliente_id"));
            Empleado empleado = empleadoDAO.obtenerPorId(rs.getLong("empleado_id"));
            VehiculoElectrico vehiculo = obtenerVehiculo(
                    rs.getLong("vehiculo_id"),
                    rs.getString("tipo_vehiculo")   // corregido
            );

            return new ContratoAlquiler(
                    cliente,
                    vehiculo,
                    rs.getDouble("precio_final"),
                    rs.getLong("id"),
                    rs.getString("forma_de_pago"),
                    rs.getDate("fecha_inicio").toLocalDate(),
                    rs.getString("estado_contrato"),
                    empleado,
                    rs.getString("periodo"),
                    rs.getInt("dias_alquilados")
            );
        } catch (ServiceException e) {
            throw new SQLException("Error al mapear contrato alquiler: " + e.getMessage());
        }
    }
}