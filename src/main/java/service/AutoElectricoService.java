package service;

import dao.AutoElectricoDAO;
import model.AutoElectrico;
import java.util.ArrayList;

public class AutoElectricoService {

    private final AutoElectricoDAO dao = new AutoElectricoDAO();

    public void guardar(AutoElectrico auto) throws ServiceException {
        if (auto.getId() == null || auto.getId().isBlank())
            throw new ServiceException("CAMPO_VACIO", "El ID del auto no puede estar vacío.");
        if (auto.getMarca() == null || auto.getMarca().isBlank())
            throw new ServiceException("CAMPO_VACIO", "La marca no puede estar vacía.");
        if (auto.getPrecioBase() <= 0)
            throw new ServiceException("PRECIO_INVALIDO", "El precio debe ser mayor a 0.");

        dao.guardar(auto);
    }

    public ArrayList<AutoElectrico> obtenerTodos() throws ServiceException {
        return dao.obtenerTodos();
    }

    public void eliminar(long id) throws ServiceException {
        dao.eliminar(id);
    }
}