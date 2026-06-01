package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import service.AkiraAssistantService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControladorAsistenteAI {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox       chatContainer;
    @FXML private TextField  campoTexto;
    @FXML private Button     btnEnviar;
    @FXML private ProgressIndicator indicadorCarga;
    @FXML private Button     btnLimpiar;

    @FXML private Button btnQ1;
    @FXML private Button btnQ2;
    @FXML private Button btnQ3;
    @FXML private Button btnQ4;
    @FXML private Button btnQ5;
    @FXML private Button btnQ6;

    private final AkiraAssistantService servicio   = new AkiraAssistantService();
    private final ExecutorService       executor   = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        indicadorCarga.setVisible(false);

        chatContainer.heightProperty().addListener((obs, old, val) ->
                scrollPane.setVvalue(1.0)
        );

        configurarPreguntasRapidas();

        agregarMensajeAsistente(
                "¡Hola! 👋 Soy Akira, tu asistente de vehículos eléctricos. " +
                        "Puedo ayudarte a encontrar el vehículo ideal, comparar opciones y orientarte sobre precios. " +
                        "¿En qué puedo ayudarte hoy?"
        );
    }

    private void configurarPreguntasRapidas() {
        btnQ1.setOnAction(e -> enviarPreguntaRapida("¿Cuál es el vehículo eléctrico más económico?"));
        btnQ2.setOnAction(e -> enviarPreguntaRapida("Quiero un auto eléctrico familiar, ¿qué me recomiendas?"));
        btnQ3.setOnAction(e -> enviarPreguntaRapida("¿Cuál tiene mayor autonomía de batería?"));
        btnQ4.setOnAction(e -> enviarPreguntaRapida("¿Qué diferencia hay entre alquiler y compra?"));
        btnQ5.setOnAction(e -> enviarPreguntaRapida("Necesito algo para uso urbano diario, ¿patineta o bicicleta?"));
        btnQ6.setOnAction(e -> enviarPreguntaRapida("¿Cuáles son las ventajas de los vehículos eléctricos?"));
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoTexto.getText().trim();
        if (texto.isEmpty()) return;
        campoTexto.clear();
        procesarMensaje(texto);
    }

    private void enviarPreguntaRapida(String pregunta) {
        procesarMensaje(pregunta);
    }

    private void procesarMensaje(String texto) {
        agregarMensajeUsuario(texto);
        setControlesActivos(false);

        executor.submit(() -> {
            try {
                String respuesta = servicio.enviarMensaje(texto);
                Platform.runLater(() -> {
                    agregarMensajeAsistente(respuesta);
                    setControlesActivos(true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    agregarMensajeAsistente(
                            "⚠️ Hubo un error al conectarme: " + ex.getMessage() +
                                    "\nVerifica tu conexión o API key."
                    );
                    setControlesActivos(true);
                });
            }
        });
    }

    @FXML
    private void limpiarChat() {
        chatContainer.getChildren().clear();
        servicio.limpiarHistorial();
        agregarMensajeAsistente(
                "Chat reiniciado. 🔄 ¿En qué puedo ayudarte?"
        );
    }

    private void agregarMensajeUsuario(String texto) {
        HBox contenedor = new HBox();
        contenedor.setAlignment(Pos.CENTER_RIGHT);
        contenedor.setPadding(new Insets(4, 12, 4, 60));

        Label burbuja = new Label(texto);
        burbuja.getStyleClass().add("burbuja-usuario");
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(420);

        contenedor.getChildren().add(burbuja);
        chatContainer.getChildren().add(contenedor);
    }

    private void agregarMensajeAsistente(String texto) {
        HBox contenedor = new HBox(8);
        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setPadding(new Insets(4, 60, 4, 12));

        Label avatar = new Label("⚡");
        avatar.getStyleClass().add("avatar-asistente");

        Label burbuja = new Label(texto);
        burbuja.getStyleClass().add("burbuja-asistente");
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(420);

        contenedor.getChildren().addAll(avatar, burbuja);
        chatContainer.getChildren().add(contenedor);
    }

    private void setControlesActivos(boolean activo) {
        campoTexto.setDisable(!activo);
        btnEnviar.setDisable(!activo);
        btnQ1.setDisable(!activo);
        btnQ2.setDisable(!activo);
        btnQ3.setDisable(!activo);
        btnQ4.setDisable(!activo);
        btnQ5.setDisable(!activo);
        btnQ6.setDisable(!activo);
        indicadorCarga.setVisible(!activo);
    }

    public void shutdown() {
        executor.shutdown();
    }
}