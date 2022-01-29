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

/** 
this is a helper class for converting User and Event object to Json string, and vice versa.
     */
public class JsonHelper {
    
    /** 
     * @param r
     * @return User
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static User extractUser(Reader r) throws JsonProcessingException, IOException {
        return new ObjectMapper().reader(User.class).readValue(r);
    }
    
    /** 
     * @param r
     * @return Event
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static Event extractEvent(Reader r) throws JsonProcessingException, IOException {
        return new ObjectMapper().reader(Event.class).readValue(r);
    }
    
    /** 
     * @param w
     * @param value
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void serialize(Writer w,Object value) throws JsonGenerationException, JsonMappingException, IOException {
        new ObjectMapper().writeValue(w, value);
    }
}
