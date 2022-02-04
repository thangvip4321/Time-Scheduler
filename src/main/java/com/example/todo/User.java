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
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    public String username;
    public String email;
    public int userID;

    @JsonCreator
    public User(
            @JsonProperty("username")String username,
            @JsonProperty("email") String email,
            @JsonProperty("userID") int userID
    )
    {
        this.username = username;
        this.email = email;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public int getUserID() {
        return userID;
    }

    @Override
    public String toString(){
        return "user " + + this.getUserID() + " : "+ this.getUsername() + " & " + this.getEmail();
    }
}
