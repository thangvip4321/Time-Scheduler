package com.example.todo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static String token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3N1ZWQgZGF0ZSI6MTY0MzIzMjIzMzU2NCwidXNlcklEIjoyLCJ1c2VybmFtZSI6ImR1YyJ9.8c0ssRTAokzcr9PRY9PX-HCGKjwWD_a9zqfY1UIVsND_I6-YGgMMwrFPvibvZVc_AIhjWjpfP4wxUy6YNU2TUw";
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}