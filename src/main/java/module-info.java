module org.example.akira {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.akira to javafx.fxml;
    exports org.example.akira;
}