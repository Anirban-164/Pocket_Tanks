module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.util;
    opens com.example.demo.util to javafx.fxml;
}