module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.web;


    opens com.example.project to javafx.fxml;
    exports com.example.project;
}