package Utilities;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Event;
import entities.User;

public class JsonHelper {
    public static User extractUser(Reader r) throws JsonProcessingException, IOException {
        return new ObjectMapper().reader(User.class).readValue(r);
    }
    public static Event extractEvent(Reader r) throws JsonProcessingException, IOException {
        return new ObjectMapper().reader(Event.class).readValue(r);
    }
    public static void serialize(Writer w,Object value) throws JsonGenerationException, JsonMappingException, IOException {
        new ObjectMapper().writeValue(w, value);
    }
}
