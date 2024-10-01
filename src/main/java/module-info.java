module com.example.messagesfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.google.gson;

    opens com.example.messagesfx.models to com.google.gson;
    opens com.example.messagesfx to javafx.fxml;
    exports com.example.messagesfx;
    exports com.example.messagesfx.models;
    exports com.example.messagesfx.utils;
    opens com.example.messagesfx.utils to com.google.gson, javafx.fxml;
    exports com.example.messagesfx.responses;
    opens com.example.messagesfx.responses to com.google.gson, javafx.fxml;
}