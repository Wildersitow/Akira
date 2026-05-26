package dao;

import model.Cliente;
import model.Empleado;
import service.ServiceException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpleadoDAO {

    private static final String ARCHIVO = "empleado.dat";

    public void guardar(Empleado empleado) throws ServiceException {
        String sql = "INSERT INTO cliente (nombre, cedula, telefono, email, contrasena, direccion) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false); // ← agrega esto

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumentoId());
            ps.setString(3, String.valueOf(empleado.getTelefono()));
            ps.setString(4, empleado.getEmail());
            ps.setString(5, empleado.getContraseña());
            ps.setString(6, null);
            ps.executeUpdate();

            con.commit(); // ← y esto

        } catch (SQLException e) {
            throw new ServiceException("ERROR_GUARDADO", "Error al guardar cliente: " + e.getMessage(), e);
        }
    }

    public Empleado buscarPorNombreUsuario(String nombreUsuario) throws ServiceException {
        try {
            ArrayList<Empleado> empleados = leer();
            for (Empleado e : empleados) {
                if (e.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                    return e;
                }
            }
            return null;
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar empleado: " + e.getMessage(), e);
        }
    }

    public Empleado buscarPorNombre(String nombre) throws ServiceException {
        try {
            ArrayList<Empleado> empleados = leer();
            for (Empleado e : empleados) {
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    return e;
                }
            }
            return null;
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar empleado: " + e.getMessage(), e);
        }
    }

    public ArrayList<Empleado> obtenerTodos() throws ServiceException {
        try {
            return leer();
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al leer empleados: " + e.getMessage(), e);
        }
    }

    public void actualizar(Empleado empleado) throws ServiceException {
        try {
            ArrayList<Empleado> empleados = leer();
            boolean encontrado = false;

            for (int i = 0; i < empleados.size(); i++) {
                if (empleados.get(i).getNombreUsuario().equals(empleado.getNombreUsuario())) {
                    empleados.set(i, empleado);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new ServiceException("EMPLEADO_NO_ENCONTRADO",
                        "No se encontró el empleado con usuario: " + empleado.getNombreUsuario());
            }

            guardarLista(empleados);

        } catch (IOException e) {
            throw new ServiceException("ERROR_ACTUALIZACION",
                    "Error al actualizar empleado: " + e.getMessage(), e);
        }
    }

    public void eliminar(String nombreUsuario) throws ServiceException {
        try {
            ArrayList<Empleado> empleados = leer();
            boolean eliminado = empleados.removeIf(a -> a.getNombreUsuario().equals(nombreUsuario));

            if (!eliminado) {
                throw new ServiceException("EMPLEADO_NO_ENCONTRADO",
                        "No se encontró el empleado");
            }

            guardarLista(empleados);

        } catch (IOException e) {
            throw new ServiceException("ERROR_ELIMINACION",
                    "Error al eliminar empleado: " + e.getMessage(), e);
        }
    }

    private void guardarLista(ArrayList<Empleado> empleados) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(empleados);
        }
    }

    private ArrayList<Empleado> leer() throws IOException {
        ArrayList<Empleado> empleados = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return empleados;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            empleados = (ArrayList<Empleado>) ois.readObject();
        } catch (EOFException e) {
            // Archivo vacío, retornar lista vacía
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al leer el archivo: clase no encontrada", e);
        }

        return empleados;
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

    private Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("contrasena"),
                rs.getString("cedula"),
                rs.getString("email"),
                "empleado",
                0,
                "",
                rs.getString("cargo"),
                rs.getDouble("salario")
        );
    }
}
