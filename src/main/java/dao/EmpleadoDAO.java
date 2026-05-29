package dao;

import model.Empleado;
import service.ServiceException;

import java.sql.*;
import java.util.ArrayList;

public class EmpleadoDAO {

    public void guardar(Empleado empleado) throws ServiceException {
        String sql = "INSERT INTO empleado (nombre, nombre_usuario, cedula, telefono, email, contrasena, rol, cargo, salario, fecha_ingreso, codigo_empleado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getNombreUsuario());
            ps.setString(3, empleado.getDocumentoId());
            ps.setString(4, String.valueOf(empleado.getTelefono()));
            ps.setString(5, empleado.getEmail());
            ps.setString(6, empleado.getContraseña());
            ps.setString(7, empleado.getRol());
            ps.setString(8, empleado.getCargo());
            ps.setDouble(9, empleado.getSalario());
            ps.setString(10, empleado.getCodigoEmpleado());
            ps.executeUpdate();
            con.commit();

            System.out.println("✓ Empleado guardado: " + empleado.getNombre());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar empleado: " + e.getMessage(), e);
        }
    }

    public ArrayList<Empleado> obtenerTodos() throws ServiceException {
        String sql = "SELECT * FROM empleado";
        ArrayList<Empleado> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al leer empleados: " + e.getMessage(), e);
        }
    }

    public void actualizar(Empleado empleado) throws ServiceException {
        String sql = "UPDATE empleado SET nombre=?, nombre_usuario=?, telefono=?, contrasena=?, rol=?, cargo=?, salario=? WHERE email=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getNombreUsuario());
            ps.setString(3, String.valueOf(empleado.getTelefono()));
            ps.setString(4, empleado.getContraseña());
            ps.setString(5, empleado.getRol());
            ps.setString(6, empleado.getCargo());
            ps.setDouble(7, empleado.getSalario());
            ps.setString(8, empleado.getEmail());

            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("EMPLEADO_NO_ENCONTRADO", "No se encontró el empleado");

            System.out.println("✓ Empleado actualizado: " + empleado.getNombre());

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar empleado: " + e.getMessage(), e);
        }
    }

    public void eliminar(String email) throws ServiceException {
        String sql = "DELETE FROM empleado WHERE email = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setString(1, email);
            int filas = ps.executeUpdate();
            con.commit();

            if (filas == 0)
                throw new ServiceException("EMPLEADO_NO_ENCONTRADO", "No se encontró el empleado");

            System.out.println("✓ Empleado eliminado: " + email);

        } catch (SQLException e) {
            throw new ServiceException("ERROR_ELIMINACION", "Error al eliminar empleado: " + e.getMessage(), e);
        }
    }

    public Empleado buscarPorEmail(String email) throws ServiceException {
        String sql = "SELECT * FROM empleado WHERE email = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar empleado: " + e.getMessage(), e);
        }
    }

    public Empleado buscarPorCedula(String cedula) throws ServiceException {
        String sql = "SELECT * FROM empleado WHERE cedula = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;

        } catch (SQLException e) {
            throw new ServiceException("ERROR_LECTURA", "Error al buscar empleado: " + e.getMessage(), e);
        }
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getString("nombre"),
                rs.getString("nombre_usuario"),
                rs.getString("contrasena"),
                rs.getString("cedula"),
                rs.getString("email"),
                rs.getString("rol"),
                rs.getInt("telefono"),
                rs.getString("codigo_empleado"),
                rs.getString("cargo"),
                rs.getDouble("salario")
        );
    }
}