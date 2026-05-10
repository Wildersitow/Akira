package service;

public class ServiceLogin extends Exception {
    private static final long serialVersionUID = 1L;

    private String codigo;

    public ServiceLogin(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public ServiceLogin(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
