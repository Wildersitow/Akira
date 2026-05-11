package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.net.URL;
import java.util.ResourceBundle;

public class ControladorMenuPrincipal implements Initializable {

    @FXML
    private Button boton_logout;
    @FXML
    private Label label_akira;
    @FXML
    private Pane pane_producto1;
    @FXML
    private Pane pane_producto2;
    @FXML
    private Pane pane_producto3;

    @Override
    public void Initializable(URL location, ResourceBundle resources) {
        boton_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
    }
}
