package Utilities;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Event;
import entities.User;
import repositories.DataRepository;
import repositories.PostgreAdapter;


public class EventReminderJob implements Job {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReminderJob.class);

    public static final String EVENTNAME = "EVENTNAME";
    public static final String COUNT = "COUNT";

    private String flag = "new object";

    private DataRepository repository = new PostgreAdapter();

    public void execute(JobExecutionContext context) throws JobExecutionException {

        Object eventIdObject = context.get("eventID"); //3

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
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

        int eid = ((Number) eventIdObject).intValue();
        Event e = repository.findEventByID(eid);
        for(String name : e.participantsList) {
            User user = repository.findUserByName(name);
            MailHelper.sendMail("reminder", "Upcomming event! "+ user.username + e.eventName + "excited?", new String[] {user.email});
        }
    }
}
