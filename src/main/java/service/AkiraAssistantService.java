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

    private static final String SYSTEM_BASE = """
        Eres Akira, el asistente virtual de una empresa de alquiler y venta de vehículos eléctricos.

        Tu misión es ayudar al usuario a:
        - Encontrar el vehículo más adecuado según sus necesidades y presupuesto.
        - Conocer precios, autonomía y características de los vehículos disponibles.
        - Comparar opciones entre categorías (auto, moto, bicicleta, patineta).
        - Entender las ventajas de los vehículos eléctricos.
        - Decidir entre alquiler y compra.

        REGLAS IMPORTANTES:
        - Solo recomienda vehículos que aparezcan en el INVENTARIO ACTUAL que se te proporciona.
        - Si el inventario está vacío, informa amablemente que no hay vehículos registrados aún.
        - Si el usuario pregunta algo general (ventajas de eléctricos, cómo cargar, etc.), responde con tu conocimiento aunque no esté en el inventario.
        - Cuando menciones precios usa el formato $X.XXX.XXX (pesos colombianos COP).
        - Responde siempre en español, de forma amigable y concisa. Usa emojis con moderación.
        - Cuando necesites más información del usuario para recomendar, haz una sola pregunta a la vez.
        """;

}
