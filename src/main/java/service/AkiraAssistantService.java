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

    private static final String API_KEY = "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";
    private static final String API_URL = System.getenv("https://api.groq.com/openai/v1/chat/completions");
    private static final String MODEL   = "llama-3.3-70b-versatile";

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
        - Si el usuario pregunta algo general, responde con tu conocimiento.
        - Cuando menciones precios usa el formato $X.XXX.XXX (pesos colombianos COP).
        - Responde siempre en español, de forma amigable y concisa. Usa emojis con moderación.
        - Cuando necesites más información, haz una sola pregunta a la vez.
        """;

    public String enviarMensaje(String mensajeUsuario) throws Exception {

        String inventario = construirInventario();
        String systemPrompt = SYSTEM_BASE + "\n\n" + inventario;

        historial.add(new String[]{"user", mensajeUsuario});

        // Construir mensajes incluyendo system
        StringBuilder mensajes = new StringBuilder("[");
        mensajes.append(String.format(
                "{\"role\":\"system\",\"content\":\"%s\"}",
                escaparJson(systemPrompt)
        ));
        for (String[] msg : historial) {
            mensajes.append(",");
            mensajes.append(String.format(
                    "{\"role\":\"%s\",\"content\":\"%s\"}",
                    msg[0], escaparJson(msg[1])
            ));
        }
        mensajes.append("]");

        String body = String.format("""
            {
              "model": "%s",
              "max_tokens": 1024,
              "temperature": 0.7,
              "messages": %s
            }
            """, MODEL, mensajes);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API (" + response.statusCode() + "): " + response.body());
        }

        String respuesta = extraerTextoRespuesta(response.body());
        historial.add(new String[]{"assistant", respuesta});
        return respuesta;
    }

    private String construirInventario() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTARIO ACTUAL DEL SISTEMA ===\n\n");

        try {
            ArrayList<AutoElectrico> autos = autoDAO.obtenerTodos();
            if (!autos.isEmpty()) {
                sb.append("AUTOS ELÉCTRICOS (").append(autos.size()).append("):\n");
                for (AutoElectrico a : autos)
                    sb.append(String.format("  • %s %s | Precio: $%.0f | Puertas: %d | Pasajeros: %d | Tracción: %s\n",
                            a.getMarca(), a.getModelo(), a.getPrecioBase(),
                            a.getNumeroPuertas(), a.getNumeroPasajeros(), a.getTraccion()));
            } else sb.append("AUTOS ELÉCTRICOS: Sin registros.\n");
        } catch (Exception e) { sb.append("AUTOS ELÉCTRICOS: Error al consultar.\n"); }

        try {
            ArrayList<MotoElectrica> motos = motoDAO.obtenerTodos();
            if (!motos.isEmpty()) {
                sb.append("MOTOS ELÉCTRICAS (").append(motos.size()).append("):\n");
                for (MotoElectrica m : motos)
                    sb.append(String.format("  • %s %s | Precio: $%.0f | Tipo: %s\n",
                            m.getMarca(), m.getModelo(), m.getPrecioBase(), m.getTipoMoto()));
            } else sb.append("MOTOS ELÉCTRICAS: Sin registros.\n");
        } catch (Exception e) { sb.append("MOTOS ELÉCTRICAS: Error al consultar.\n"); }

        try {
            ArrayList<BicicletaElectrica> bicis = biciDAO.obtenerTodos();
            if (!bicis.isEmpty()) {
                sb.append("BICICLETAS ELÉCTRICAS (").append(bicis.size()).append("):\n");
                for (BicicletaElectrica b : bicis)
                    sb.append(String.format("  • %s %s | Precio: $%.0f | Marchas: %d | Asistencia: %s\n",
                            b.getMarca(), b.getModelo(), b.getPrecioBase(),
                            b.getNumeroMarchas(), b.getTipoAsistencia()));
            } else sb.append("BICICLETAS ELÉCTRICAS: Sin registros.\n");
        } catch (Exception e) { sb.append("BICICLETAS ELÉCTRICAS: Error al consultar.\n"); }

        try {
            ArrayList<PatinetaElectrica> patinetas = patiDAO.obtenerTodos();
            if (!patinetas.isEmpty()) {
                sb.append("PATINETAS ELÉCTRICAS (").append(patinetas.size()).append("):\n");
                for (PatinetaElectrica p : patinetas)
                    sb.append(String.format("  • %s %s | Precio: $%.0f | Plegable: %s\n",
                            p.getMarca(), p.getModelo(), p.getPrecioBase(),
                            p.isEsPlegable() ? "Sí" : "No"));
            } else sb.append("PATINETAS ELÉCTRICAS: Sin registros.\n");
        } catch (Exception e) { sb.append("PATINETAS ELÉCTRICAS: Error al consultar.\n"); }

        sb.append("\n=== FIN DEL INVENTARIO ===");
        return sb.toString();
    }

    public void limpiarHistorial() {
        historial.clear();
    }

    private String extraerTextoRespuesta(String json) {
        int idx = json.indexOf("\"content\":");
        if (idx == -1) return "No pude obtener una respuesta.";
        int inicio = json.indexOf("\"", idx + 10) + 1;
        int fin = inicio;
        while (fin < json.length()) {
            if (json.charAt(fin) == '\\') { fin += 2; continue; }
            if (json.charAt(fin) == '"') break;
            fin++;
        }
        return desescaparJson(json.substring(inicio, fin));
    }

    private String escaparJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String desescaparJson(String text) {
        return text
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}