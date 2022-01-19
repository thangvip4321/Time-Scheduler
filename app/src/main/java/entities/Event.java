package entities;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
    // We dont need eventID in this class, but there should be one in the database
    // actually we do need eventID to send invite link to user, since none of these attributes are unique
    public List<String> participantsList;
    public String organizer;
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
