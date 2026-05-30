package service;

import dao.PatinetaElectricaDAO;
import model.PatinetaElectrica;
import java.util.ArrayList;

public class PatinetaElectricaService {

    private final PatinetaElectricaDAO dao = new PatinetaElectricaDAO();

    public void guardar(PatinetaElectrica patineta) throws ServiceException {
        if (patineta.getId() == null || patineta.getId().isBlank())
            throw new ServiceException("CAMPO_VACIO", "El ID de la patineta no puede estar vacío.");
        if (patineta.getMarca() == null || patineta.getMarca().isBlank())
            throw new ServiceException("CAMPO_VACIO", "La marca no puede estar vacía.");
        if (patineta.getPrecioBase() <= 0)
            throw new ServiceException("PRECIO_INVALIDO", "El precio debe ser mayor a 0.");

        dao.guardar(patineta);
    }

    public ArrayList<PatinetaElectrica> obtenerTodos() throws ServiceException {
        return dao.obtenerTodos();
    }

    public void eliminar(long id) throws ServiceException {
        dao.eliminar(id);
    }
}