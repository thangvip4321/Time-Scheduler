package ReminderAPI;

import entities.Event;
import entities.User;

/**
 * Convenient for using or implementing the code with interface class
 * 
 * <p>
 * Reminder does not store an actual instance of <code>JobImpl</code> class, but
 * instead allows you to define an instance of one, through the use of a <code>Reminder</code>.
 * </p>
 * 
 * <p>
 * <code>JobImpl</code> have implemented this interface for the convenience
 * <code>Job</code>s have a name and group associated with them, which
 * should uniquely identify them within a single <code>{@link Scheduler}</code>.
 * </p>
 * 
 * <p>
 * <code>Trigger</code>s are the 'mechanism' by which <code>Job</code>s
 * are scheduled. Many <code>Trigger</code>s can point to the same <code>Job</code>,
 * but a single <code>Trigger</code> can only point to one <code>Job</code>.
 * </p>
 * 
 * @see Event
 * @see User
 * @see #sendMail(int)
 * 
 * @author Nguyen Tuan Ngoc
 */
public interface Reminder {

    /*
     * send mail with message at specific time by organizer 
     */
    void sendMail(int eid);

    /*
     * send mail with message before 30 minutes by organizer 
     */
    void sendMailBefore1Hour(User user, Event event) throws Exception;

    /*
     * send mail with message before 30 minutes by organizer 
     */
    void sendMailBefore30Min(User user, Event event) throws Exception;

    /*
     * send mail with message before 15 minutes by organizer 
     */
    void sendMailBefore15Min(User user, Event event) throws Exception;

    /*
     * send mail with message before 10 mintes by organizer
     */
    void sendMailBefore10Min(User user, Event event) throws Exception;

    /* 
     * send mail with message before 5 minutes by organizer
     */
    void sendMailBefore5Min(User user, Event event) throws Exception;

}
