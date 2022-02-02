module com.example.todo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.todo to javafx.fxml;
    exports com.example.todo;

}