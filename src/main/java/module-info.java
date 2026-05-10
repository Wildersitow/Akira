module view.akira {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens view.akira to javafx.fxml;
    exports view.akira;
}