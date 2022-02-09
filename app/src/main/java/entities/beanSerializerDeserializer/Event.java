package entities.beanSerializerDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.beanutils.converters.SqlTimestampConverter;

/**
 * Note: In order to make the object to be as Serializer and also Deserializer
 * 
 * <p> We use these
 * {@link JsonSerializer}, {@link JsonDeserializer} to transform java code to json and json to java code </p>
 * 
 * <input>
 *      ref=https://stackoverflow.com/questions/7161638/how-do-i-use-a-custom-serializer-with-jackson
 * </input>
 * 
 * @author Nguyen Tuan Ngoc
 */
public class Event {
    // We dont need eventID in this class, but there should be one in the database
    // actually we do need eventID to send invite link to user, since none of these attributes are unique
    public List<String> participantsList;
    public String organizer;
    // JacksonConfig date = new JacksonConfig();
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

@Provider
class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public JacksonConfig() {
        objectMapper = new ObjectMapper();
        SimpleModule s = new SimpleModule();
        s.addSerializer(Timestamp.class, new TimestampSerializerTypeHandler());
        s.addDeserializer(Timestamp.class, new TimestampDeserializerTypeHandler());
        objectMapper.registerModule(s);
    };

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}

class TimestampDeserializerTypeHandler extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext ds) throws IOException, JsonProcessingException {
        SqlTimestampConverter s = new SqlTimestampConverter();
        String value = jp.getValueAsString(null);
        if(value != null && !value.isEmpty() && !value.equals("null"))
            return (Timestamp) s.convert(Timestamp.class, value);
        return null;
    }

    @Override
    public Class<Timestamp> handledType() {
        return Timestamp.class;
    }
}

class TimestampSerializerTypeHandler extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        String stringValue = value.toString();
        if(stringValue != null && !stringValue.isEmpty() && !stringValue.equals("null")) {
            jgen.writeString(stringValue);
        } else {
            jgen.writeNull();
        }
    }

    @Override
    public Class<Timestamp> handledType() {
        return Timestamp.class;
    }
}