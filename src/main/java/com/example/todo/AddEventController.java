package com.example.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class AddEventController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField eventName;
    @FXML
    private TextField timePicker;
    @FXML
    private TextField endTimePicker;
    @FXML
    private ListView<LocalEvent> myListView;
    @FXML
    private ChoiceBox<String> priorityPicker;
    @FXML
    private ChoiceBox<String> reminderPicker;
    @FXML
    private TextField participantsList;
    @FXML
    private TextField locationField;


    ObservableList<LocalEvent>list = FXCollections.observableArrayList();
    @FXML
    void initialize() throws JsonProcessingException {
        String priorities[] = {"low", "medium", "high"};
        String reminders[]= {"1 week","3 days","1 day","1 hour","30 minutes","15 minutes","10 minutes","5 minutes"};
        loadEvent();
        priorityPicker.getItems().addAll(priorities);
        reminderPicker.getItems().addAll(reminders);
        reminderPicker.setValue("3 days");
        priorityPicker.setValue("low");
        datePicker.setValue(LocalDate.now());
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
                .sslContext(sslContext).followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        return client;
    }

    private void refresh(){
        eventName.setText("");
        timePicker.setText("");
        datePicker.setValue(LocalDate.now());
        priorityPicker.setValue("low");
        participantsList.setText("");
        endTimePicker.setText("");
    }

    private void loadColor(){
        myListView.setCellFactory(new Callback<ListView<LocalEvent>, ListCell<LocalEvent>>() {
            @Override
            public ListCell<LocalEvent> call(ListView<LocalEvent> myObjectListView) {
                ListCell<LocalEvent> cell = new ListCell<LocalEvent>(){
                    @Override
                    protected void updateItem(LocalEvent obj, boolean empty) {
                        super.updateItem(obj, empty);
                        if(!empty){
                            if(obj.getPriority().toLowerCase().equals("low")){  //favorite view
                                setStyle("-fx-background-color: #a1ffa1;");
                            } else if(obj.getPriority().toLowerCase().equals("medium")){  //normal view
                                setStyle("-fx-background-color: #ffffa1;");
                            }
                            else if(obj.getPriority().toLowerCase().equals("high")){
                                setStyle("-fx-background-color: #ffa1a1;");
                            }
                            setText(obj.toString());
                        }
                        else { //empty view
                            setText(null);
                            setStyle("-fx-background-color: white;");
                        }
                    }
                };
                return cell;
            }
        });
    }

    public void addEvent(ActionEvent event) throws JsonProcessingException {
            LocalEvent newEvent = new LocalEvent(eventName.getText(), datePicker.getValue(),
                    timePicker.getText(),endTimePicker.getText(),priorityPicker.getValue(),
                    participantsList.getText().split(",",0), locationField.getText());
            String remindTime = reminderPicker.getValue();
            HttpResponse<String> response =  sendAddHttpRequest(newEvent,remindTime);
            if(response.statusCode() == 200){
                setID(newEvent,response.body());
                list.add(newEvent);
                myListView.setItems(list);
                loadColor();
            }
            else{
                showErrorMessage(response.body());
            }
        refresh();
    }

    public void removeEvent(ActionEvent event) {
        ObservableList<LocalEvent> selectedItems = myListView.getSelectionModel().getSelectedItems();
        int deletedEventID = 0;
        for (LocalEvent a : selectedItems) {
            deletedEventID = a.getEventID();
        }
        sendDeleteHttpRequest(deletedEventID);
        int selectionID = myListView.getSelectionModel().getSelectedIndex();
        myListView.getItems().remove(selectionID);
    }

    public void loadEvent() throws JsonProcessingException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/event"))
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
        LocalEvent[] events = mapper.readValue(json, LocalEvent[].class);
        for (LocalEvent event : events) {
            list.add(event);
        }
        myListView.setItems(list);
        loadColor();
    }

    public static void setID(LocalEvent event, String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(responseBody, Map.class);
        int eventID =  (int) map.get("eventID");
        event.setEventID(eventID);
    }

    private HttpResponse<String> createHttpResponse(HashMap<String, Object>values){
        var objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/event"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
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
        return response;
    }

    private HttpResponse<String> createDeleteHttpResponse(int eventID){
        HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create("https://164.90.181.13:8080/event"))
                .DELETE()
                .setHeader("token",Main.token)
                .setHeader("eventID", String.valueOf(eventID))
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

    private HttpResponse<String> sendAddHttpRequest(LocalEvent event, String remindTime) throws JsonProcessingException {
        //send http POST request
        String startTime = event.getDate().toString() + "T" + event.getTime()+":00";
        startTime = LocalDateTime.parse(startTime).toInstant(ZoneOffset.UTC).toString();
        String finalStartTime = startTime;
        String endTime = event.getDate().toString() + "T" + event.getEndTime()+":00";
        endTime = LocalDateTime.parse(endTime).toInstant(ZoneOffset.UTC).toString();
        String finalEndTime = endTime;
        var values = new HashMap<String, Object>() {{
            put("name", event.getName());
            put("organizer", "duc");
            put("start from", finalStartTime);
            put("end at",finalEndTime);
            put("priority", event.getPriority());
            put("participants list", event.getParticipantsList());
            put("remind before", remindTime);
            put("location", event.getLocation());
        }};
        HttpResponse<String> response = createHttpResponse(values);
        return response;
    }

    private void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText("error");
        alert.showAndWait();
    }

    private void sendDeleteHttpRequest(int eventID){
        HttpResponse<String> response = createDeleteHttpResponse(eventID);
        if(response.statusCode() == 200){
            System.out.println("Delete task successfully");
        }
    }
}
