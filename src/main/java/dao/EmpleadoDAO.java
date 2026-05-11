package dao;

import model.Empleado;
import service.ServiceException;

import java.io.IOException;
import java.util.ArrayList;

public class EmpleadoDAO {

    private static final String ARCHIVO = "empleado.dat";

    public void guardar(Empleado empleado) throws ServiceException {
        try {
            ArrayList<Empleado> empleados = leer();

            // Verificar si el nombre de usuario ya existe
            for (Empleado e : empleados) {
                if (e.getNombreUsuario().equalsIgnoreCase(empleado.getNombreUsuario())) {
                    throw new ServiceException("USUARIO_DUPLICADO",
                            "El nombre de usuario '" + empleado.getNombreUsuario() + "' ya está en uso");
                }
            }

            empleados.add(empleado);
            guardarLista(empleados);

        } catch (IOException e) {
            throw new ServiceException("ERROR_GUARDADO",
                    "Error al guardar empleado: " + e.getMessage(), e);
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
                    "Error al buscar administrador: " + e.getMessage(), e);
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
                    "Error al buscar administrador: " + e.getMessage(), e);
        }
    }



}
