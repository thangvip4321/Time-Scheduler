package entities;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Event {
    public List<User> participantsList;
    public User organizer;
    public Date date;
    public String eventName;
    Event(List<User> participantsList, User organizer, Date date,String eventName) {
        this.participantsList = participantsList;
        this.organizer = organizer;
        this.date = date;
        this.eventName = eventName;
    }
    
}
