package com.example.todo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonIgnoreProperties(value = {"remind before"})
public class LocalEvent {
    public String name;
    public LocalDate date;
    public String time;
    public String priority;
    public int eventID;
    public String[] participantsList;
    public String endTime;
    public String location;
    @JsonCreator
    public LocalEvent( @JsonProperty("name")String eventName,
                       @JsonProperty("start from") String start,
                       @JsonProperty("end at") String end,
                       @JsonProperty("location") String location,
                       @JsonProperty("eventID")int eventID,
                       @JsonProperty("organizer") String organizer,
                       @JsonProperty("priority") String priority,
                       @JsonProperty("participants list") String[] participantsList
    )
    {
        this.location = location;
        this.endTime = Instant.parse(end).atZone(ZoneId.systemDefault()).toLocalTime().toString();
        this.date = Instant.parse(start).atZone(ZoneId.systemDefault()).toLocalDate();
        this.time = Instant.parse(start).atZone(ZoneId.systemDefault()).toLocalTime().toString();
        this.name = eventName;
        this.eventID = eventID;
        this.priority = priority;
        this.participantsList = participantsList;
    }

    public LocalEvent(String name, LocalDate date, String time,String endTime, String priority, String[] participantsList, String location) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
        this.priority = priority;
        this.participantsList = participantsList;
        this.location = location;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String[] getParticipantsList() {
        return participantsList;
    }

    public String getParticipantsString(){
        String result = "";
        for(String participant: participantsList){
            result += participant + ",";
        }
        return result;
    }

    @Override
    public String toString(){
        return "At " + this.getTime() + " on " + this.getDate()
                .format(DateTimeFormatter.ofPattern("MMM-dd-yyyy")) +" : You have " + this.getName().toUpperCase()
                + " with priority: "+this.getPriority().toUpperCase() + " " + "The participants list includes: "
                + this.getParticipantsString() + "The event starts at: " + this.getTime() + " and ends at: " + this.getEndTime();
    }
}

class CustomInstantDeserializer extends StdDeserializer<Instant> {
    public CustomInstantDeserializer(Class<?> vc) {
        super(vc);
    }
    public CustomInstantDeserializer(){
        this(null);
    }
    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        String date = jp.getText();
        return Timestamp.valueOf(date).toInstant();
    }
}
