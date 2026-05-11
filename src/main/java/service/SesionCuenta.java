package service;

import model.Persona;

public class SesionCuenta {

    private static Persona usuarioActual = null;

    public static void setUsuarioActual(Persona usuario) {
        usuarioActual = usuario;
    }

    public static Persona getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static String getRolActual() {
        if (usuarioActual != null) {
            return usuarioActual.getRol();
        }
        return null;
    }

    public static boolean esAdministrador() {
        return usuarioActual != null &&
                "administrador".equalsIgnoreCase(usuarioActual.getRol());
    }

    public static boolean esCliente() {
        return usuarioActual != null &&
                "cliente".equalsIgnoreCase(usuarioActual.getRol());
    }

}
