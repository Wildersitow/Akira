package service;

import dao.AutoElectricoDAO;
import dao.MotoElectricaDAO;
import dao.BicicletaElectricaDAO;
import dao.PatinetaElectricaDAO;
import model.AutoElectrico;
import model.MotoElectrica;
import model.BicicletaElectrica;
import model.PatinetaElectrica;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AkiraAssistantService {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_KEY = "TU_API_KEY_AQUI";
    private static final String MODEL   = "claude-sonnet-4-20250514";

    private final List<String[]> historial = new ArrayList<>();

    private final AutoElectricoDAO      autoDAO = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO = new PatinetaElectricaDAO();

}
