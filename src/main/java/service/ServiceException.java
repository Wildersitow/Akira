package service;

public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String codigo;

    public ServiceException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    
    

    public ServiceException(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
