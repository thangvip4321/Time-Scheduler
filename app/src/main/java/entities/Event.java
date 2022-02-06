package entities;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

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
import static gradle_tish_embedded.App.reminder;

/**
 * <p>This is just a placeholder class for storing Event entity when doing business logic code. Thus all properties are set to public.
 * 
 * <p> This class behaves similarly to a struct in C. The purpose of this class is just for other outside functions to change its state, that's all.
 * No methods, only attributes!
 * @see entities.User
 */
public class Event {

    private final static String[] predefinedPriority = {"LOW","MEDIUM","HIGH"};
    private final static String[] predefinedReminddate = {"1 week","3 days","1 day","1 hour","30 minutes","15 minutes","10 minutes","5 minutes"};

    private final static String jsonEndAt= "end at";
    private final static String jsonStartFrom= "start from";
    private final static String jsonEventName= "name";
    private final static String jsonLocation= "location";
    private final static String jsonParticipantsList= "participants list";
    private final static String jsonRemindBefore= "remind before";

    // We dont need eventID in this class, but there should be one in the database
    // actually we do need eventID to send invite link to user, since none of these attributes are unique
    @JsonProperty(value=jsonParticipantsList,required = true)
    public List<String> participantsList;

    public String organizer;
    @JsonProperty(value=jsonStartFrom,required = true)
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    public Instant startFrom;

    @JsonProperty(value=jsonEndAt,required = true)
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    public Instant endAt;
    @JsonProperty(value=jsonEventName, required=true)
    public String eventName;
    public Integer eventID;
    public String priority;
    public String location;
    @JsonProperty(value=jsonRemindBefore,required= true)
    public String remindBefore;
        
    
    
    
    /**
     * <p>This is a default constructor for making event class.
     * <p> Those {@link JsonProperty} are used for deserializing from JSON objects back to {@link event} object, because we have to send this Event object to client
     * via JSON.
     * @param eventID eventID of the event. <strong>Reminder:</strong> , again this class is just a placeholder, so sometimes we dont
     * even need to specify this ID.
     * @param eventName   Name of the event
     * @param organizer name of the organizer. 
     * @param startFrom the time when the event will start. <strong>Reminder:</strong> when parsing JSON object into this Object, the date must
     * follow this format <code>YYYY-MM-DDTHH:MM:SS.sZ</code>
     * @param endAt the time when the event will end. <strong>Reminder:</strong> when parsing JSON object into this Object, the date must
     * follow this format <code>YYYY-MM-DDTHH:MM:SS.sZ</code>
     * @param location the location of the event. This can be any string, even a link to a Zoom meeting
     * @param priority priority of the event. Should be 1 these 3 strings: <code>LOW</code>,<code>MEDIUM</code> or <code>HIGH</code>.
     * Throw an {@link IllegalArgumentException} if priority is anything else. case is not important
     * @param participantsList list of participants in the event. <strong>Reminder: </strong> this participantsList absolutely 
     * does not reflect the actual event list of the event.
    * @param remindBefore a string represent when the user wants to be reminded
     * 

     */
    @JsonCreator
    // we added a lot of required statement here, i wonder if it is correct?
    public Event(@JsonProperty("eventID")int eventID,
                 @JsonProperty(value=jsonEventName, required=true)String eventName,
                 @JsonProperty("organizer") String organizer,
                 @JsonProperty(value= jsonStartFrom,required = true) Instant startTime,
                 @JsonProperty(value=jsonEndAt,required = true) Instant endTime,
                 @JsonProperty(jsonLocation) String location,
                 @JsonProperty(value="priority",required=true) String priority,
                 @JsonProperty(value=jsonParticipantsList,required = true) List<String> participantsList,
                 @JsonProperty(value=jsonRemindBefore,required= true) String remindBefore 
                 )
    {
        this.participantsList = participantsList;
        this.organizer = organizer;
        this.startFrom = startTime;
        this.endAt = endTime;
        this.eventName = eventName;
        this.eventID = eventID;
        if(!Arrays.stream(predefinedPriority).anyMatch(priority::equalsIgnoreCase)){
            throw new IllegalArgumentException("priority type must be LOW,MEDIUM, or HIGH");
        }
        // should it be null?
        if(!Arrays.stream(predefinedReminddate).anyMatch(remindBefore::equalsIgnoreCase)){
            throw new IllegalArgumentException("remind time type must be 1 day,....");
        }
        this.priority = priority;
        this.remindBefore = remindBefore;
    }



    public Event(int eventID,
    String eventName,
    String organizer,
    Instant startTime,
    Instant endTime,
    String location,
    String priority,
    List<String> participantsList){ 
        this(eventID, eventName, organizer, startTime, endTime, location, priority, participantsList, "1 day");
    }

    // i have doubt about creating this event entities like this. add too much null will catch other dev
    // off guard when using our code,e.g they may trigger anything related to startFrom attribute which is null
    public Event(int eventID) {
        // this spaghetti code is not tolerated, we should set all to null?
        this(eventID,null,null,null,null,null,"LOW",null,"1 day");
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
        String date = jp.getText();
        try {
            return Instant.parse(date);
            // DateTimeFormatter.ISO_INSTANT
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong format for date, the format must follow UTC standard");
        }
    }

}
class CustomInstantSerializer extends StdSerializer<Instant>{

    protected CustomInstantSerializer(Class<Instant> t) {
        super(t);
    }



    public CustomInstantSerializer(){
        this(null);
    }


    @Override
    public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeString(value.toString());
    }

}
