package dao;

import model.Cliente;
import model.Persona;
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

    private ArrayList<Persona> leerCuentas() throws IOException {
        ArrayList<Persona> cuentas = new ArrayList<>();
        File archivo = new File(ARCHIVO_CUENTAS);

        if (!archivo.exists()) {
            return cuentas;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            cuentas = (ArrayList<Persona>) ois.readObject();
        } catch (EOFException e) {
            // Archivo vacío, retornar lista vacía
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al leer el archivo de cuentas: clase no encontrada", e);
        }

        return cuentas;
    }

        public Cliente crearCuentaParaCliente(String nombreUsuario) throws ServiceException {
        // Verificar que el cliente exista
        Cliente cliente = buscarPorNombreUsuario(nombreUsuario);
        if (cliente == null) {
            throw new ServiceException("CLIENTE_NO_EXISTE",
                    "No se encontró el cliente con usuario: " + nombreUsuario);
        }
        // Verificar que no tenga cuenta ya
            Cliente cuentaExistente = buscarCuentaClientePorUsuario(nombreUsuario);
        if (cuentaExistente != null) {
            throw new ServiceException("CUENTA_EXISTENTE",
                    "El cliente ya tiene una cuenta registrada");
        }
        Cliente nuevaCuenta = new Cliente("", nombreUsuario, "", "", "", "cliente", 0, "", 0.0, 0, new ArrayList<>());
        guardarCuenta(nuevaCuenta);
        System.out.println("✓ Cuenta creada para cliente: " + nombreUsuario);
        return nuevaCuenta;
    }

    public ArrayList<Cliente> obtenerTodos() throws ServiceException {
        try {
            return leer();
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al leer clientes: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cliente cliente) throws ServiceException {
        try {
            ArrayList<Cliente> clientes = leer();
            boolean encontrado = false;

            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getNombreUsuario().equals(cliente.getNombreUsuario())) {
                    clientes.set(i, cliente);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                throw new ServiceException("CLIENTE_NO_ENCONTRADO",
                        "No se encontró el cliente con usuario: " + cliente.getNombreUsuario());
            }
            guardarLista(clientes);

            System.out.println("✓ Cliente actualizado: " + cliente.getNombreUsuario());

        } catch (IOException e) {
            throw new ServiceException("ERROR_ACTUALIZACION",
                    "Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    private void guardarListaCuentas(ArrayList<Persona> cuentas) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS))) {
            oos.writeObject(cuentas);
        }
    }

    public Cliente buscarPorNombreUsuario(String nombreUsuario) throws ServiceException {
        try {
            ArrayList<Cliente> clientes = leer();
            for (Cliente c : clientes) {
                if (c.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                    return c;
                }
            }
            return null;
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarCuentaClientePorUsuario(String nombreUsuario) throws ServiceException {
        try {
            ArrayList<Persona> cuentas = leerCuentas();
            for (Persona c : cuentas) {
                if (c.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                    return (Cliente) c;
                }
            }
            return null;
        } catch (IOException e) {
            throw new ServiceException("ERROR_LECTURA",
                    "Error al buscar cuenta: " + e.getMessage(), e);
        }
    }

    public void guardarCuenta(Cliente cuenta) throws ServiceException {
        try {
            ArrayList<Persona> cuentas = leerCuentas();
            boolean actualizado = false;

            // Buscar si ya existe la cuenta y actualizarla
            for (int i = 0; i < cuentas.size(); i++) {
                if (cuentas.get(i).getNombreUsuario().equals(cuenta.getNombreUsuario())) {
                    cuentas.set(i, cuenta);
                    actualizado = true;
                    break;
                }
            }
            if (!actualizado) {
                cuentas.add(cuenta);
            }

            guardarListaCuentas(cuentas);

            System.out.println("✓ Cuenta guardada/actualizada: " + cuenta.getNombreUsuario());

        } catch (IOException e) {
            throw new ServiceException("ERROR_GUARDADO",
                    "Error al guardar cuenta: " + e.getMessage(), e);
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
