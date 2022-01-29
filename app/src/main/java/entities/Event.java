package entities;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


/**
 * <p>This is just a placeholder class for storing Event entity when doing business logic code. Thus all properties are set to public.
 * 
 * <p> This class behaves similarly to a struct in C. The purpose of this class is just for other outside functions to change its state, that's all.
 * No methods, only attributes!
 * @see entities.User
 */
public class Event {
    private static String[] predefinedPriority = {"LOW","MEDIUM","HIGH"};
    // We dont need eventID in this class, but there should be one in the database
    // actually we do need eventID to send invite link to user, since none of these attributes are unique
    @JsonProperty("participants list")
    public List<String> participantsList;

    public String organizer;
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    public Instant date;
    @JsonProperty("name")
    public String eventName;
    public Integer eventID;
    public String priority;
        /**
     * <p>This is a default constructor for making event class.
     * <p> Those {@link JsonProperty} are used for deserializing from JSON objects back to {@link event} object.
     * @param eventName   Name of the event
     * @param organizer name of the organizer. 
     * @param participantsList list of participants in the event. <strong>Reminder: </strong> this participantsList absolutely 
     * does not reflect the actual event list of the event.
     * @param eventID eventID of the event. <strong>Reminder:</strong> , again this class is just a placeholder, so sometimes we dont
     * even need to specify this ID.
     * @param priority priority of the event. Should be 1 these 3 strings: <code>LOW</code>,<code>MEDIUM</code> or <code>HIGH</code>.
     * Throw an {@link IllegalArgumentException} if priority is anything else.
     * @param date the time when the event will start. <strong>Reminder:</strong> when parsing JSON object into this Object, the date must
     * follow this format <code>YYYY-MM-DD HH:MM:SS.s</code>
     */
    @JsonCreator
    public Event(@JsonProperty("eventID")int eventID,
                 @JsonProperty("name")String eventName,
                 @JsonProperty("organizer") String organizer,
                 @JsonProperty("date") Instant time,
                 @JsonProperty("priority") String priority,
                 @JsonProperty("participants list") List<String> participantsList
)
    {
        this.participantsList = participantsList;
        this.organizer = organizer;
        this.date = time;
        this.eventName = eventName;
        this.eventID = eventID;
        if(!Arrays.stream(predefinedPriority).anyMatch(priority::equals)){
            throw new IllegalArgumentException("priority type must be LOW,MEDIUM, or HIGH");
        }
        this.priority = priority;
    }
    public Event(int eventID) {
        this(eventID,null,null,null,"LOW",null);
    }
}

class CustomInstantDeserializer extends StdDeserializer<Instant>{

    public CustomInstantDeserializer(Class<?> vc) {
        super(vc);
    }

    public CustomInstantDeserializer(){
        this(null);
    }
    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        // TODO Auto-generated method stub
        String date = jp.getText();
        try {
            return Timestamp.valueOf(date).toInstant();
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong format for date, the format must be YYYY-MM-DD HH:MM:SS.s");
        }
    }

}
class CustomInstantSerializer extends StdSerializer<Instant>{

    protected CustomInstantSerializer(Class<Instant> t) {
        super(t);
        //TODO Auto-generated constructor stub
    }



    public CustomInstantSerializer(){
        this(null);
    }


    @Override
    public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeString(Timestamp.from(value).toString());
    }

}
