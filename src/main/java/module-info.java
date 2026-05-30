module view.akira {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens view.akira to javafx.fxml;
    opens view to javafx.fxml, javafx.graphics;
    exports view.akira;
    exports view;
}