package service;

import dao.*;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceVehiculo {

    private final AutoElectricoDAO      autoDAO = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO = new PatinetaElectricaDAO();

    public List<VehiculoElectrico> obtenerDisponibles() throws ServiceException {
        List<VehiculoElectrico> lista = new ArrayList<>();
        for (AutoElectrico v      : autoDAO.obtenerTodos()) if (v.estaDisponible()) lista.add(v);
        for (MotoElectrica v      : motoDAO.obtenerTodos()) if (v.estaDisponible()) lista.add(v);
        for (BicicletaElectrica v : biciDAO.obtenerTodos()) if (v.estaDisponible()) lista.add(v);
        for (PatinetaElectrica v  : patiDAO.obtenerTodos()) if (v.estaDisponible()) lista.add(v);
        return lista;
    }
}