package dao;

import model.Empleado;
import service.ServiceException;

import java.io.*;
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



}
