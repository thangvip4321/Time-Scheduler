package entities;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
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

public class Event {
    // We dont need eventID in this class, but there should be one in the database
    // actually we do need eventID to send invite link to user, since none of these attributes are unique
    public List<String> participantsList;
    public String organizer;
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    public Instant date;
    public String eventName;
    public Integer eventID;
    public int priority;
    @JsonCreator
    public Event(@JsonProperty("eventID")int eventID,
                 @JsonProperty("name")String eventName,
                 @JsonProperty("organizer") String organizer,
                 @JsonProperty("date") Instant time,
                 @JsonProperty("priority") int priority,
                 @JsonProperty("participants list") List<String> participantsList
)
    {
        this.participantsList = participantsList;
        this.organizer = organizer;
        this.date = time;
        this.eventName = eventName;
        this.eventID = eventID;
        this.priority = priority;
    }
    public Event(int eventID) {
        this(eventID,null,null,null,-1,null);
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
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        String date = jp.getText();
        return Timestamp.valueOf(date).toInstant();
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
