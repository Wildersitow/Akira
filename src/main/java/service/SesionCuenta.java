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

}
