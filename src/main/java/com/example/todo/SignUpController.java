package com.example.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Optional;

public class SignUpController {
    @FXML
    private Button signUpBtn;
    @FXML
    private TextField signUpEmail;
    @FXML
    private PasswordField signUpPassword;
    @FXML
    private TextField signUpUserName;
    @FXML
    void initialize(){
        signUpBtn.setOnAction(event -> {
            try {
                createUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void createUser() throws Exception {
//        //testing without send request
//        showSuccessfulMessage(" ");

        String email = signUpEmail.getText();
        String userName = signUpUserName.getText();
        String password = signUpPassword.getText();
        // send HTTP POST request to sign up https://164.90.181.13:8080
        var values = new HashMap<String, String>() {{
            put("username",userName);
            put("email", email);
            put("password",password);
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // skip certificate check

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpClient client = createClientWithNoCertCheck();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            showSuccessfulMessage(response.body());
        }
        else{
            showErrorMessage(response.body());
        }
    }

    private HttpClient createClientWithNoCertCheck (){
        TrustManager[] trustAllCerts =new TrustManager[]{
                new X509ExtendedTrustManager()
                {
                    public X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }

                    public void checkClientTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type)
                    {
                    }

                    public void checkServerTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type)
                    {
                    }
                    public void checkClientTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type,
                            final Socket a_socket)
                    {
                    }
                    public void checkServerTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type,
                            final Socket a_socket)
                    {
                    }
                    public void checkClientTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type,
                            final SSLEngine a_engine)
                    {
                    }
                    public void checkServerTrusted(
                            final X509Certificate[] a_certificates,
                            final String a_auth_type,
                            final SSLEngine a_engine)
                    {
                    }
                }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert sslContext != null;
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        //send request
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        return client;
    }

    private void showLoginScreen(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        try{
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        ///////////////////
    }

    private void showSuccessfulMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("alert");
        alert.setHeaderText(message);
        alert.setContentText("Please close the window and re-run the application");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            signUpBtn.getScene().getWindow().hide();
            showLoginScreen();
        }
    }

    private void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText("error");
        alert.showAndWait();
    }
}
