package com.example.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public class LoginController {
    @FXML
    private Button loginBtnLogin;
    @FXML
    private Button loginBtnSignUp;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField loginUserName;

    @FXML
    void initialize(){
        //take user to sign up screen
        loginBtnSignUp.setOnAction(event -> {
            loginBtnLogin.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("signup.fxml"));
            try{
                loader.load();
            }catch (IOException e){
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });
        
        loginBtnLogin.setOnAction(event -> {
            String username = loginUserName.getText();
            String password = loginPassword.getText();
            //send http POST request
            var values = new HashMap<String, String>() {{
                put("username", username);
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
                    .uri(URI.create("https://164.90.181.13:8080/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpClient client = createClientWithNoCertCheck();
            HttpResponse<String> response = null;
            try {
                response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // check for successful login
            if(response.statusCode() == 200){
                System.out.println(response.body());
                if(response.body().equals("login successfully as admin\n")){
                    Main.token = response.headers().allValues("token").get(0);
                    showAdminPage();
                }
                //set token
                else if(response.body().equals("login successfully\n")){
                    Main.token = response.headers().allValues("token").get(0);
                    showAddEventScreen();
                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid login");
                alert.showAndWait();
            }
        });
    }

    private void showAdminPage(){
        loginBtnLogin.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin.fxml"));
        try{
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void showAddEventScreen(){
        loginBtnLogin.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addEvent.fxml"));
        try{
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
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
//            throw new Exception("qwe");
            e.printStackTrace();
        }
        //send request
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        return client;
    }
}