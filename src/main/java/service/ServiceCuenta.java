package service;

import dao.ClienteDAO;
import dao.EmpleadoDAO;
import javafx.scene.control.Alert;
import model.Cliente;
import model.Empleado;
import view.UtilidadesFX;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ServiceCuenta {

    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;
    private final UtilidadesFX utilidades;   // agregar esto

    public ServiceCuenta() {
        this.clienteDAO = new ClienteDAO();
        this.empleadoDAO = new EmpleadoDAO();
        this.utilidades = new UtilidadesFX(); // agregar esto
    }

    public void registrarUsuario(ActionEvent event, String nombreUsuario, String correo, String documentoid, String contraseña, String rol) throws ServiceException {
        System.out.println("\n=== SERVICIO: Iniciando registro de usuario ===");
        System.out.println("Correo: " + correo);
        System.out.println("Usuario: " + nombreUsuario);
        System.out.println("Documento de identidad: " + documentoid);
        System.out.println("Rol: " + rol);

        try {

            if (!esCorreoValido(correo)) {
                throw new ServiceException("CORREO_INVALIDO",
                        "El formato del correo electrónico no es válido");
            }

            if (nombreUsuario.length() < 4) {
                throw new ServiceException("USUARIO_CORTO",
                        "El nombre de usuario debe tener al menos 4 caracteres");
            }

            if (contraseña.length() < 6) {
                throw new ServiceException("CONTRASENA_CORTA",
                        "La contraseña debe tener al menos 6 caracteres");
            }

            if (clienteDAO.buscarPorNombreUsuario(nombreUsuario) != null ||
                    empleadoDAO.buscarPorNombreUsuario(nombreUsuario) != null) {
                throw new ServiceException("USUARIO_DUPLICADO",
                        "El nombre de usuario '" + nombreUsuario + "' ya está en uso");
            }

            if (rol.equalsIgnoreCase("cliente")) {
                System.out.println("Creando nuevo Cliente...");

                Cliente nuevoCliente = new Cliente("", nombreUsuario, contraseña, documentoid, correo, "cliente", 0, "", 0.0, 0, new ArrayList<>());

                clienteDAO.guardar(nuevoCliente);
                System.out.println("Cliente guardado exitosamente!");

                // Mostrar confirmación y redirigir
                view.UtilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso",
                        "¡Bienvenido " + nombreUsuario + "! Tu cuenta de cliente ha sido creada.");

                view.UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion/IniciarSesión.fxml");

            } else if (rol.equalsIgnoreCase("empleado")) {
                System.out.println("Creando nuevo Empleado...");

                Empleado nuevoEmpleado = new Empleado("", nombreUsuario, contraseña, documentoid, correo, "empleado", 0, "", "", 0.0);

                // Guardar en repositorio
                empleadoDAO.guardar(nuevoEmpleado);
                System.out.println("Empleado guardado exitosamente!");

                // Mostrar confirmación y redirigir
                UtilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso",
                        "¡Bienvenido " + nombreUsuario + "! Tu cuenta de empleado ha sido creada.");

                // Ir a inicio de sesión
                view.UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion/IniciarSesión.fxml");

            } else {
                throw new ServiceException("ROL_INVALIDO", "El rol especificado no es válido");
            }

        } catch (ServiceException e) {
            System.err.println("Error de persistencia: " + e.getMessage());
            throw new ServiceException(e.getCodigo(), e.getMessage(), e);

        } catch (Exception e) {
            System.err.println("Error inesperado en registro: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("ERROR_REGISTRO",
                    "Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    public void iniciarSesion(ActionEvent event, String nombreUsuario, String contraseña)
            throws ServiceException {

        System.out.println("\n=== SERVICIO: Iniciando sesión ===");
        System.out.println("Usuario: " + nombreUsuario);

        try {
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                throw new ServiceException("USUARIO_VACIO",
                        "El nombre de usuario no puede estar vacío");
            }

            if (contraseña == null || contraseña.trim().isEmpty()) {
                throw new ServiceException("CONTRASEÑA_VACIA",
                        "La contraseña no puede estar vacía");
            }


            Cliente cliente = clienteDAO.buscarPorNombreUsuario(nombreUsuario);

            if (cliente != null) {
                System.out.println("Usuario encontrado como Cliente");

                if (cliente.verificarContraseña(contraseña)) {
                    System.out.println("Contraseña correcta - Acceso concedido");

                    SesionCuenta.setUsuarioActual(cliente);

                    view.UtilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido",
                            "¡Bienvenido " + cliente.getNombre() + "!");

                    view.UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion/MenuPrincipal.fxml");
                    return;

                } else {
                    System.out.println("Contraseña incorrecta");
                    throw new ServiceException("ERROR_CONTRA_INCORRECTA",
                            "La contraseña ingresada es incorrecta");
                }
            }

            Empleado empleado = EmpleadoDAO.buscarPorNombreUsuario(nombreUsuario);

            if (empleado != null) {
                System.out.println("Usuario encontrado como Administrador");

                if (empleado.verificarContraseña(contraseña)) {
                    System.out.println("Contraseña correcta - Acceso concedido");

                    SesionCuenta.setUsuarioActual(empleado);

                    view.UtilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido",
                            "¡Bienvenido Empleado " + empleado.getNombre() + "!");

                    view.UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion");
                    return;

                } else {
                    System.out.println("Contraseña incorrecta");
                    throw new ServiceException("ERROR_CONTRA_INCORRECTA",
                            "La contraseña ingresada es incorrecta");
                }
            }

            System.out.println("Usuario no encontrado en ningún repositorio");
            throw new ServiceException("ERROR_USUARIO_NO_EXISTE",
                    "El usuario '" + nombreUsuario + "' no existe en el sistema");

        } catch (ServiceException e) {
            System.err.println("Error de persistencia: " + e.getMessage());
            throw new ServiceException(e.getCodigo(), e.getMessage(), e);

        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            System.err.println("Error inesperado en inicio de sesión: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("ERROR_INICIO_SESION",
                    "Error al iniciar sesión: " + e.getMessage(), e);
        }
    }
    private boolean esCorreoValido(String correo) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return correo != null && correo.matches(regex);
    }
}
