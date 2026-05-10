module view.akira {
    requires javafx.controls;
    requires javafx.fxml;


    opens view.akira to javafx.fxml;
    exports view.akira;
}