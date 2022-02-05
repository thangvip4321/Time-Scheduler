package entities.serializerDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * <p>
 * {@link JsonSerializer} ,{@link JsonDeserializer} 
 * </p>
 * <p>
 * <u>This is the link of the code</u>
 * https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
 * </p>
 */
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
    // Enum remind time {15 phut, 1 tieng, 4 tieng, 1 ngay, 1 tuan}
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

class CustomInstantSerializer extends JsonSerializer<Instant> {

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        String str = fmt.format(value);

        gen.writeString(str);
    }
}

class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Instant.from(fmt.parse(p.getText()));
    }
}