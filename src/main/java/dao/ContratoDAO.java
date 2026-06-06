package dao;

import model.*;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContratoDAO {

    private final ClienteDAO  clienteDAO  = new ClienteDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public void guardar(Contrato contrato, String tipoVehiculo) throws ServiceException {
        String sql = "{call akira.sp_registrar_contrato(?, ?, ?, ?, ?, ?)}";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            CallableStatement cs = con.prepareCall(sql);

            cs.setDouble(1, contrato.getPrecioFinal());
            cs.setString(2, contrato.getFormaDePago());
            cs.setLong(3,   contrato.getCliente().getId());
            cs.setLong(4,   contrato.getEmpleado().getId());
            cs.setLong(5,   Long.parseLong(contrato.getVehiculoElectrico().getId()));
            cs.setString(6, tipoVehiculo.toUpperCase());

            cs.execute();
            con.commit();

            System.out.println("✓ Contrato registrado, cliente id: "
                    + contrato.getCliente().getId());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO",
                    "Error al guardar contrato: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public ArrayList<Contrato> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM contrato";
        ArrayList<Contrato> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer contratos: " + e.getMessage(), e);
        }
    }

    public Contrato obtenerPorId(long id) throws ServiceException {
        String sql = "SELECT * FROM contrato WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);

            throw new ServiceException("CONTRATO_NO_ENCONTRADO", "No se encontró el contrato con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar contrato: " + e.getMessage(), e);
        }
    }

    public ArrayList<Contrato> obtenerPorCliente(long clienteId) throws ServiceException {
        String sql = "SELECT * FROM contrato WHERE cliente_id = ?";
        ArrayList<Contrato> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar contratos del cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(long id) throws ServiceException {
        String sql = "DELETE FROM contrato WHERE id = ?";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("CONTRATO_NO_ENCONTRADO", "No se encontró el contrato con id: " + id);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar contrato: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void actualizarCampos(long id, Map<String, Object> campos) throws ServiceException {
        if (campos == null || campos.isEmpty())
            throw new ServiceException("SIN_CAMPOS", "No se especificaron campos a actualizar.");

        StringBuilder sql = new StringBuilder("UPDATE contrato SET ");
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
                throw new ServiceException("CONTRATO_NO_ENCONTRADO", "No se encontró el contrato con id: " + id);

            System.out.println("✓ Contrato actualizado, campos: " + campos.keySet());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar contrato: " + e.getMessage(), e);
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

    private Contrato mapear(ResultSet rs) throws SQLException {
        try {
            Cliente  cliente  = clienteDAO.obtenerPorId(rs.getLong("cliente_id"));
            Empleado empleado = empleadoDAO.obtenerPorId(rs.getLong("empleado_id"));
            VehiculoElectrico vehiculo = obtenerVehiculo(
                    rs.getLong("vehiculo_id"),
                    rs.getString("tipo_vehiculo")
            );

            return new Contrato(
                    cliente,
                    vehiculo,
                    rs.getDouble("precio_total"),
                    rs.getLong("id"),
                    rs.getString("forma_pago"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("estado_contrato"),
                    empleado
            );
        } catch (ServiceException e) {
            throw new SQLException("Error al mapear contrato: " + e.getMessage());
        }
    }
}