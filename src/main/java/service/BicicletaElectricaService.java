package service;

import dao.BicicletaElectricaDAO;
import model.BicicletaElectrica;
import java.util.ArrayList;

public class BicicletaElectricaService {

    private final BicicletaElectricaDAO dao = new BicicletaElectricaDAO();

    public void guardar(BicicletaElectrica bici) throws ServiceException {
        if (bici.getId() == null || bici.getId().isBlank())
            throw new ServiceException("CAMPO_VACIO", "El ID de la bicicleta no puede estar vacío.");
        if (bici.getMarca() == null || bici.getMarca().isBlank())
            throw new ServiceException("CAMPO_VACIO", "La marca no puede estar vacía.");
        if (bici.getPrecioBase() <= 0)
            throw new ServiceException("PRECIO_INVALIDO", "El precio debe ser mayor a 0.");

        dao.guardar(bici);
    }

    public ArrayList<BicicletaElectrica> obtenerTodos() throws ServiceException {
        return dao.obtenerTodos();
    }

    public void eliminar(long id) throws ServiceException {
        dao.eliminar(id);
    }
}