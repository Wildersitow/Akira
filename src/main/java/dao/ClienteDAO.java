package dao;

import model.Cliente;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class ClienteDAO {

    public void guardar(Cliente cliente) throws ServiceException {
        String sql = "INSERT INTO cliente (nombre, nombre_usuario, cedula, telefono, email, contrasena, rol, direccion, licencia_conducir, historial_credito, puntos_fidelidad) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"});

            ps.setString(1,  cliente.getNombre());
            ps.setString(2,  cliente.getNombreUsuario());
            ps.setString(3,  cliente.getDocumentoId());
            ps.setString(4,  String.valueOf(cliente.getTelefono()));
            ps.setString(5,  cliente.getEmail());
            ps.setString(6,  cliente.getContraseña());
            ps.setString(7,  cliente.getRol());
            ps.setString(8,  null);
            ps.setString(9,  cliente.getLicenciaConducir());
            ps.setDouble(10, cliente.getHistorialCredito());
            ps.setInt(11,    cliente.getPuntosFidelidad());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) cliente.setId(rs.getLong(1));

            con.commit();

            System.out.println("✓ Cliente guardado: " + cliente.getNombreUsuario() + " | id: " + cliente.getId());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar cliente: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public ArrayList<Cliente> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM cliente";
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer clientes: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cliente cliente) throws ServiceException {
        String sql = "UPDATE cliente SET nombre=?, nombre_usuario=?, telefono=?, contrasena=?, rol=?, licencia_conducir=?, historial_credito=?, puntos_fidelidad=? WHERE email=?";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getNombreUsuario());
            ps.setString(3, String.valueOf(cliente.getTelefono()));
            ps.setString(4, cliente.getContraseña());
            ps.setString(5, cliente.getRol());
            ps.setString(6, cliente.getLicenciaConducir());
            ps.setDouble(7, cliente.getHistorialCredito());
            ps.setInt(8,    cliente.getPuntosFidelidad());
            ps.setString(9, cliente.getEmail());

            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("CLIENTE_NO_ENCONTRADO", "No se encontró el cliente");

            System.out.println("✓ Cliente actualizado: " + cliente.getNombreUsuario());

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar cliente: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void eliminar(String email) throws ServiceException {
        String sql = "DELETE FROM cliente WHERE email = ?";
        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("CLIENTE_NO_ENCONTRADO", "No se encontró el cliente");

            System.out.println("✓ Cliente eliminado: " + email);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar cliente: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Cliente buscarPorEmail(String email) throws ServiceException {
        String sql = "SELECT * FROM cliente WHERE email = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorCedula(String cedula) throws ServiceException {
        String sql = "SELECT * FROM cliente WHERE cedula = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorNombre(String nombre) throws ServiceException {
        String sql = "SELECT * FROM cliente WHERE LOWER(nombre) = LOWER(?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente obtenerPorId(long id) throws ServiceException {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);

            throw new ServiceException("CLIENTE_NO_ENCONTRADO", "No se encontró el cliente con id: " + id);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getString("nombre"),
                rs.getString("nombre_usuario"),
                rs.getString("contrasena"),
                rs.getString("cedula"),
                rs.getString("email"),
                rs.getString("rol"),
                rs.getInt("telefono"),
                rs.getString("licencia_conducir"),
                rs.getDouble("historial_credito"),
                rs.getInt("puntos_fidelidad"),
                new ArrayList<>()
        );
        cliente.setId(rs.getLong("id"));
        return cliente;
    }
}
