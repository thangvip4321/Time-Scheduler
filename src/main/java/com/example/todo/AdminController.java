package com.example.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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


public class AdminController {
    @FXML
    private ListView<User> adminListView;
    ObservableList<User> list = FXCollections.observableArrayList();
    @FXML
    void initialize() throws JsonProcessingException {
        loadData();
    }
    private void loadData() throws JsonProcessingException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/admin"))
                .GET()
                .setHeader("token",Main.token)
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
        ObjectMapper mapper = new ObjectMapper();
        String json = response.body();
        System.out.println(response.body());
        User[] users = mapper.readValue(json, User[].class);
        for (User user : users) {
            list.add(user);
        }
        adminListView.setItems(list);
    }

    @FXML
    public void removeUser(ActionEvent event) {
        ObservableList<User> selectedItems = adminListView.getSelectionModel().getSelectedItems();
        String deletedUsername = "";
        for (User a : selectedItems) {
            deletedUsername = a.getUsername();
        }
        sendDeleteHttpRequest(deletedUsername);
        int selectionID = adminListView.getSelectionModel().getSelectedIndex();
        adminListView.getItems().remove(selectionID);
    }

    private void sendDeleteHttpRequest(String userName){
        HttpResponse<String> response = createDeleteHttpResponse(userName);
        if(response.statusCode() == 200){
            System.out.println("Delete user successfully");
        }
    }

    private HttpResponse<String> createDeleteHttpResponse(String username){
        HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/admin"))
                .DELETE()
                .setHeader("token",Main.token)
                .setHeader("username", String.valueOf(username))
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
        return response;
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
