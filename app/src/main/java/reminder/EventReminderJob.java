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
 * This class is responsible for reminding participants when the event is near.
 * Note: 

 * @see reminder.EventReminderJob.execute
 * @see reminder.QuartzReminder.buildJobDetail
 * 
 * @author Nguyen Tuan Ngoc, Nguyen Duc Thang
 */

public class EventReminderJob implements Job {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReminderJob.class);

    
    public static final String EVENTNAME = "EVENTNAME";

    /**
     * <p> This fields is instantiated to work as a mapping key for {@code JobBuilder} </p>
     */
    public static final String COUNT = "COUNT";

    /**
     * <p> This fields is instantiated to work as a mapping key for {@code JobBuilder} </p>
     */
    public static final String REPO_USED = "REPO";

    /**
     * <p> This fields is instantiated to work as a mapping key for {@code JobBuilder} </p>
     */
    public static final String EVENT_ID = "eventID";

    /**
     * This is a special flag indicate to be marked as {@value String}
     */
    private String flag = "new object";

    /**
     * This simply utilize some helper functions from the {@link DataRepository}
     */
    private DataRepository repository;

    /**The {@code execute} function is called when the corresponding {@code Trigger} fire, which means there is 5 minutes until
     * the event start, for example.
     * <p>
     *   This function will interact with {@code DataRepository} to retrieve
     * events and participants, hence it needs several value in the {@code JobDataMap} to run properly!.
     * @see EVENTNAME,EVENT_ID,REPO_USED
     * </p>
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
