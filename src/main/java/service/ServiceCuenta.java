package service;

import javafx.scene.control.Alert;
import model.Cliente;
import model.Empleado;
import java.awt.event.ActionEvent;

public class ServiceCuenta {

    public void registrarUsuario(ActionEvent event, String correo, String nombreUsuario, String contraseña, String rol) throws LogicaException {}

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

            // ========== BUSCAR USUARIO ==========

            // Primero buscar en clientes
            Cliente cliente = clienteDAO.buscarPorNombreUsuario(nombreUsuario);

            if (cliente != null) {
                System.out.println("Usuario encontrado como Cliente");

                // Verificar contraseña
                if (cliente.verificarContraseña(contraseña)) {
                    System.out.println("Contraseña correcta - Acceso concedido");

                    // Guardar sesión actual
                    SesionCuenta.setUsuarioActual(cliente);

                    // Redirigir a dashboard de cliente
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

            // Si no es cliente, buscar en administradores
            Empleado empleado = EmpleadoDAO.buscarPorNombreUsuario(nombreUsuario);

            if (empleado != null) {
                System.out.println("Usuario encontrado como Administrador");

                // Verificar contraseña
                if (empleado.verificarContraseña(contraseña)) {
                    System.out.println("Contraseña correcta - Acceso concedido");

                    // Guardar sesión actual
                    SesionCuenta.setUsuarioActual(empleado);

                    // Redirigir a dashboard de administrador
                    view.UtilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido",
                            "¡Bienvenido Administrador " + empleado.getNombre() + "!");

                    view.UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion");
                    return;

                } else {
                    System.out.println("Contraseña incorrecta");
                    throw new ServiceException("ERROR_CONTRA_INCORRECTA",
                            "La contraseña ingresada es incorrecta");
                }
            }

            // Si no se encontró ni como cliente ni como administrador
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
}
