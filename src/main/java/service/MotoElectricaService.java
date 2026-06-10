package service;

import dao.MotoElectricaDAO;
import model.MotoElectrica;
import java.util.ArrayList;

public class MotoElectricaService {

    private final MotoElectricaDAO dao = new MotoElectricaDAO();

    public void guardar(MotoElectrica moto) throws ServiceException {
        if (moto.getId() == null || moto.getId().isBlank())
            throw new ServiceException("CAMPO_VACIO", "El ID de la moto no puede estar vacío.");
        if (moto.getMarca() == null || moto.getMarca().isBlank())
            throw new ServiceException("CAMPO_VACIO", "La marca no puede estar vacía.");
        if (moto.getPrecioBase() <= 0)
            throw new ServiceException("PRECIO_INVALIDO", "El precio debe ser mayor a 0.");

        dao.guardar(moto);
    }

    public ArrayList<MotoElectrica> obtenerTodos() throws ServiceException {
        return dao.obtenerTodos();
    }

    public void eliminar(long id) throws ServiceException {
        dao.eliminar(id);
    }
}