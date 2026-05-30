package view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

public class ControladorMenuAgregarVehiculo {

    @FXML private ComboBox<String> comboTipoVehiculo;
    @FXML private Pane paneAuto;
    @FXML private Pane paneMoto;
    @FXML private Pane panePatineta;
    @FXML private Pane paneBicicleta;

    @FXML
    public void initialize() {
        comboTipoVehiculo.getItems().addAll(
                "Auto Eléctrico",
                "Moto Eléctrica",
                "Patineta Eléctrica",
                "Bicicleta Eléctrica"
        );

        // Ocultar todos al inicio
        ocultarTodosLosPanes();

        // Listener que reacciona al cambio
        comboTipoVehiculo.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, nuevo) -> {
                    ocultarTodosLosPanes();
                    switch (nuevo) {
                        case "Auto Eléctrico"      -> paneAuto.setVisible(true);
                        case "Moto Eléctrica"      -> paneMoto.setVisible(true);
                        case "Patineta Eléctrica"  -> panePatineta.setVisible(true);
                        case "Bicicleta Eléctrica" -> paneBicicleta.setVisible(true);
                    }
                });
    }

    private void ocultarTodosLosPanes() {
        paneAuto.setVisible(false);
        paneMoto.setVisible(false);
        panePatineta.setVisible(false);
        paneBicicleta.setVisible(false);
    }

}
