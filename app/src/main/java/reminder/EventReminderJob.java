package reminder;

import java.util.InputMismatchException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utilities.MailHelper;
import entities.Event;
import entities.User;
import repositories.DataRepository;

/**
 * This class is the materials for building the {@code QuartzReminder} class.
 * 
 * <ul>
 *  The field of the class is used for comparing the key of this field with the key of {@link QueartzReminder}
 *   <u>
 *      We can also use this class for helping us to know what this method {@code execute} for.
 *  </u>
 * </ul>
 * 
 * @see reminder.EventReminderJob.execute
 * @see reminder.QuartzReminder.buildJobDetail
 * 
 * @author Nguyen Tuan Ngoc
 */
public class EventReminderJob implements Job {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReminderJob.class);

    /**
     * <p> This fields is instantiated to work as a mapping key for {@code JobBuilder} </p>
     */
    public static final String EVENTNAME = "EVENTNAME";
    public static final String COUNT = "COUNT";
    public static final String REPO_USED = "REPO";
    public static final String EVENT_ID = "eventID";

    /**
     * This is a special flag indicate to be marked as {@value String}
     */
    private String flag = "new object";

    /**
     * This simply utilize some helper functions from the {@link DataRepository}
     */
    private DataRepository repository;

    /**
     * <p>
     *  This function works like a mark to map the same key of the {@code JobDataMap} if they are the same keys.
     *  Otherwise {@link buildJobDetail} won't build/create the event. Since the user who triggers the event is
     *  not the same one, so the event job can not be created through the wrong key set.
     * </p>
     * 
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {


        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int eid  = (int) dataMap.get(EVENT_ID); //3
        repository = (DataRepository) dataMap.get(REPO_USED);
        //fetch parameters from JobDataMap
		String eventName = dataMap.getString(EVENTNAME);
        
		int count = dataMap.getInt(COUNT);
        JobKey jobKey = context.getJobDetail().getKey();
		System.out.println(jobKey+": "+ eventName+"-"+count+": flag="+flag);
        count++;
        //add next counter to JobDataMap
		dataMap.put(COUNT, count);
        flag= "object changed";

        LOGGER.info("send mail to each participant in the event: ");

            Event e = repository.findEventByID(eid);
            if(e == null) throw new InputMismatchException("the eid does not exist");
            User organizer = repository.findUserByName(e.organizer);
            MailHelper.sendMail("reminder", "Upcoming event for user"+ organizer.username+":"+ e.eventName, new String[] {organizer.email});
            for(String name : e.participantsList) {
                System.err.println("name is"+name);
                User user = repository.findUserByName(name);
                MailHelper.sendMail("reminder", "Upcoming event for user"+ user.username+":"+ e.eventName, new String[] {user.email});
            }

    }
}
