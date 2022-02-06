package Utilities;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.Map;

import javax.servlet.ServletException;

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
    static Map<String,Object> exampleEvent = Map.ofEntries(
        new AbstractMap.SimpleEntry<String,Object>("name","the event name"),
        new AbstractMap.SimpleEntry<String,Object>("organizer","name of event organizer"),
        new AbstractMap.SimpleEntry<String,Object>("start from","must follow UTC standard, i.e YYYY-MM-DDTHH:MM:SSZ"),
        new AbstractMap.SimpleEntry<String,Object>("end at","must follow UTC standard"),
        new AbstractMap.SimpleEntry<String,Object>("priority","must be LOW,MEDIUM, or HIGH"),
        new AbstractMap.SimpleEntry<String,Object>("participants list","[participant1,participant2]"),
        new AbstractMap.SimpleEntry<String,Object>("remind before","the time in string")
    );
    static Map<String,Object> exampleUser = Map.ofEntries(
        new AbstractMap.SimpleEntry<String,Object>("username","the name of your user"),
        new AbstractMap.SimpleEntry<String,Object>("password","your password"),
        new AbstractMap.SimpleEntry<String,Object>("email","(optional, omit if you are only logging in) your_email@domain"),
        new AbstractMap.SimpleEntry<String,Object>("userID","(optional) this is what the our server will return")
    );

    /** 
     * @param r
     * @return User
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static User extractUser(Reader r) throws JsonProcessingException, ServletException,IOException {
        try {
            return new ObjectMapper().readerFor(User.class).readValue(r);
        } catch (JsonProcessingException e) {
            throw new ServletException("wrong format for user type, here is an example: "+printMapWithNewLine("", exampleUser));
        }    
    }
    
    /** 
     * @param r
     * @return Event
     * @throws JsonProcessingException
     * @throws IOException
     * @throws ServletException
     */
    public static Event extractEvent(Reader r) throws JsonProcessingException,IOException ,ServletException {
        try {
            return new ObjectMapper().readerFor(Event.class).readValue(r);
        } catch (JsonProcessingException e) {
            throw new ServletException("wrong format for event type, here is an example: "+printMapWithNewLine("", exampleEvent));
        }
    }
    private static String printMapWithNewLine(String mapName,Map<String,Object> map){
        StringBuilder result = new StringBuilder();
        String newline="\n  ";
        result.append(mapName+":{");
            for (var entry : map.entrySet()) {
                result.append(newline).append(entry.getKey()).append(" : ").append(entry.getValue()).append(",");
            }
            result.append("\n}");
        return result.toString();
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
