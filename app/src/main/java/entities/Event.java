package entities;

import java.math.BigInteger;
import java.util.Date;

public class Event {
    User[] participantsList;
    User organizer;
    Date date;
    String eventName;
    BigInteger eventID;
}
