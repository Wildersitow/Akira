package dao;

import model.Cliente;
import service.ServiceException;

import java.io.*;
import java.util.ArrayList;

public class ClienteDAO {

    private static final String ARCHIVO_CLIENTES = "clientes.dat";
    private static final String ARCHIVO_CUENTAS = "cuentas.dat";

    public void guardar(Cliente cliente) throws ServiceException {
        try {
            ArrayList<Cliente> clientes = leer();

            // Verificar si el nombre de usuario ya existe
            for (Cliente c : clientes) {
                if (c.getNombreUsuario().equalsIgnoreCase(cliente.getNombreUsuario())) {
                    throw new ServiceException("USUARIO_DUPLICADO",
                            "El nombre de usuario '" + cliente.getNombreUsuario() + "' ya está en uso");
                }
            }

            // Verificar si el documento de identidad ya existe
            if (cliente.getDocumentoId() != null && !cliente.getDocumentoId().isEmpty()) {
                for (Cliente c : clientes) {
                    if (c.getDocumentoId().equals(cliente.getDocumentoId())) {
                        throw new ServiceException("CEDULA_DUPLICADA",
                                "El documento de identidad '" + cliente.getDocumentoId() + "' ya está registrado");
                    }
                }
            }

            clientes.add(cliente);
            guardarLista(clientes);

            System.out.println("✓ Cliente guardado: " + cliente.getNombreUsuario());

        } catch (IOException e) {
            throw new ServiceException("ERROR_GUARDADO",
                    "Error al guardar cliente: " + e.getMessage(), e);
        }
    }

    private void guardarLista(ArrayList<Cliente> clientes) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CLIENTES))) {
            oos.writeObject(clientes);
        }
    }

    //Metodo privado encargado de leer el documento

    private ArrayList<Cliente> leer() throws IOException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        File archivo = new File(ARCHIVO_CLIENTES);

        if (!archivo.exists()) {
            return clientes;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            clientes = (ArrayList<Cliente>) ois.readObject();
        } catch (EOFException e) {
            // Archivo vacío, retornar lista vacía
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al leer el archivo: clase no encontrada", e);
        }

        return clientes;
    }
}
