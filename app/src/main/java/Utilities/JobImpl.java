package Utilities;

import org.quartz.Trigger;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.expression.FieldExpressionFactory;
import com.cronutils.model.field.value.SpecialChar;

import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ReminderAPI.Reminder;
import entities.Event;
import entities.User;
import repositories.DataRepository;
import repositories.PostgreAdapter;

public class JobImpl implements Reminder {

    /**
     * Case insensitive Logger constant used to pops up in the console.
     * <p> This logger works as it use the JobImpl as a place to show Logger in console window. </p>
     * 
     * {@link org.slf4j.Logger;}
     * 
     * @see Logger
     * @see JobImpl
     */
    private static final Logger logger = LoggerFactory.getLogger(JobImpl.class);

    /**
     * it use {@literal PostgresAdapter} to instantiate on DataRepository which is an interface class
     * 
     * @see DataRepository
     */
    private DataRepository repo = new PostgreAdapter();

    public void sendNotification(int eid, Instant startAt) throws SchedulerException {
        //  collect all user that needs to be sent a reminder
        // only an outline
        Scheduler scheduler = getScheduler();

        // JobDetail job = JobBuilder.newJob(JobImpl.class).withIdentity(repo.findEventByID(eid).eventName,repo.).
        // String eventName = repo.findEventByID(eid).eventName;
        // // Trigger trigger = TriggerBuilder.newTrigger().withIdentity(eveName);

        // User organizerName = repo.findOwnerOfEvent(eid);
        JobDetail jobDetail = buildJobDetail(eid);
        Trigger trigger = buildJobTrigger(jobDetail, startAt);
        scheduler.scheduleJob(jobDetail, trigger);

        scheduler.start();

        try {
            // run for 292 billion years
            Thread.sleep(Long.MAX_VALUE);
            // executing...
        } catch(Exception e) {
            e.printStackTrace();
        }

        scheduler.shutdown(true);
    }   

    public boolean UnScheduler(Scheduler scheduler) throws SchedulerException {
        return scheduler.unscheduleJob(buildTriggerKey("myTrigger", "myTriggerGroup"));
    }

    /**
     *
     * 
     * 
     */
    public TriggerKey buildTriggerKey(String myTrigger, String myTriggerGroup) {
        return TriggerKey.triggerKey(myTrigger, myTriggerGroup);
    }

    /**
     * <code>buildJobDetail</code> is used to instantiate {@link JobDetail}s.
     * <p>
     * Use this helper method <code>buildJobDetail</code> to implement 
     * <code>{@link org.quartz.JobDetail}</code> return
     * <code>{@link org.quartz.Job}</code>, and which is implemented by
     * <code>{@link org.quartz.JobBuilder}</code>.
     * </p>
     * 
     * <p>
     * Return the description given to the <code>JobDetail</code> instance by its
     * creator (if any).
     * </p>
	 * @param 
	 * @return JobDetail <code>{@link JobDetail}</code>.
     */
    public JobDetail buildJobDetail(int eventID) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("abc", 2);
        jobDataMap.put("eventID", eventID);

        return JobBuilder.newJob(EventReminderJob.class)
            .withIdentity(((Integer)eventID).toString())
            .requestRecovery(false)
            .withDescription("Send Email for Upcoming Event")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build();
    }

    /**
     * <code>Trigger</code> is used to instantiate {@link Trigger}s.
     * <p>
     * Return the description given to the <code>Job</code> instance by its
     * creator (if any).
     * </p>
     * 
     * @see JobDetail <code>{@link JobDetail}</code>.
     * @see Instant <code>{@link java.time.instant}</code>.
     * @return Trigger <code>{@link Trigger}</code>.
     */
    private Trigger buildJobTrigger(JobDetail jobDetail, Instant startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "eventID?????")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(Instant.from(startAt)))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    /**
     * <code>TriggerBuilder</code> is used to instantiate {@link Trigger}s.
     * <p>
     * Return the description given to the <code>Job</code> instance by its
     * creator (if any).
     * </p>
     * 
     * <pre>
     *      Trigger trigger = buildJobTrigger(jobDetail, cron);
     * </pre>
     * 
     * @see JobDetail <code>{@link JobDetail}</code>.
     * @see Cron <code>{@link com.cronutils.model.Cron}</code>.
     * @return Trigger <code>{@link Trigger}</code>.
     */
    private Trigger buildJobTrigger(JobDetail jobDetail, Cron cron) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(),"group2")
                .withDescription("triggerDescription")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron.asString()))
                .build();
    }


    public Scheduler getScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();  
        return schedulerFactory.getScheduler();
    }

    public SchedulerMetaData getSchedulerMetaData() throws SchedulerException {
        return getScheduler().getMetaData();
    }

    private static CronDefinition defineOwnCronDefinition() {
        // define your own cron: arbitrary fields are allowed and last field can be optional
        return CronDefinitionBuilder.defineCron().withSeconds().and().withMinutes().and().withHours().and().withDayOfMonth()
            .supportsHash().supportsL().supportsW().and().withMonth().and().withDayOfWeek().withIntMapping(7, 0) 
            // we
            // support
            // non-standard
            // non-zero-based
            // numbers!
            .supportsHash().supportsL().supportsW().and().withYear().optional().and().instance();
      }

      /**
     * Returns an Instant that is offset by a number of days from now.
     *
     * @param offsetInDays integer number of days to offset by
     * @return an Instant offset by {@code offsetInDays} days
     */
    public static Instant getInstantDaysOffsetFromNow(long offsetInDays) {
        return Instant.now().plus(Duration.ofDays(offsetInDays));
    }


    /**
     * Returns an Instant that is offset by a number of days before now.
     *
     * @param offsetInDays integer number of days to offset by
     * @return an Instant offset by {@code offsetInDays} days
     */
    public static Instant getInstantDaysOffsetBeforeNow(long offsetInDays) {
        return Instant.now().minus(Duration.ofDays(offsetInDays));
    }

    public static ZonedDateTime getCurrentTimeByZoneId(String region) {
        ZoneId zone = ZoneId.of(region);
        ZonedDateTime date = ZonedDateTime.now(zone);
        return date;
    }

    public ZonedDateTime convertZonedDateTime(ZonedDateTime sourceDate, String destZone) {

        ZoneId destZoneId = ZoneId.of(destZone);
        ZonedDateTime destDate = sourceDate.withZoneSameInstant(destZoneId);

        return destDate;
    }

    /**
     * Formats a datetime stamp from an {@code instant} using a formatting pattern.
     *
     * <p>Note: a formatting pattern containing 'a' (for the period; AM/PM) is treated differently at noon/midday.
     * Using that pattern with a datetime whose time falls on "12:00 PM" will cause it to be formatted as "12:00 NOON".</p>
     *
     * @param instant  the instant to be formatted
     * @param timeZone the time zone to compute local datetime
     * @param pattern  formatting pattern, see Oracle docs for DateTimeFormatter for pattern table
     * @return the formatted datetime stamp string
     */
    public static String formatInstant(Instant instant, String timeZone, String pattern) {
        if (instant == null || timeZone == null || pattern == null) {
            return "";
        }
        ZonedDateTime zonedDateTime = getCurrentTimeByZoneId(timeZone);
        String processedPattern = pattern;
        if (zonedDateTime.getHour() == 12 && zonedDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return zonedDateTime.format(formatter);
    }

    public Cron CronBuilder(Instant starttime) throws Exception {
        
        ZonedDateTime reminder = starttime.atZone(ZoneId.of("UTC"));
        int year = reminder.getYear();
        int month = reminder.getMonthValue();
        // DayOfWeek dow = reminder.getDayOfWeek();
        // int DoW = dow.getValue();
        int DoM = reminder.getDayOfMonth();
        int hour = reminder.getHour();
        int min = reminder.getMinute();
        int sec = reminder.getSecond();

        CronDefinition cronDefinition = defineOwnCronDefinition();
        return CronBuilder.cron(cronDefinition).withYear(FieldExpressionFactory.on(year))
                    .withDoM(between(SpecialChar.L, DoM))
                    .withMonth(on(month))
                    .withDoW(questionMark())
                    .withHour(on(hour))
                    .withMinute(on(min))
                    .withSecond(on(sec))
                    .instance();
    }

    @Override
    public void sendMailBefore5Min(User user, Event event) throws Exception {

        logger.info("preparing to fire alarm for reminding clients");
        Instant schedTime = Instant.from(event.date).minusSeconds(5*60);
        Cron cron = CronBuilder(schedTime);
        sendNotification(event.eventID, schedTime);
        System.out.println(cron.asString());

    }

    @Override
    public void sendMailBefore10Min(User user, Event event) throws Exception {

        logger.info("preparing to fire alarm for reminding clients");
        Instant schedTime = Instant.from(event.date).minus(10, ChronoUnit.MINUTES);
        sendNotification(event.eventID, schedTime);
    }

    @Override
    public void sendMailBefore15Min(User user, Event event) throws Exception {

        logger.info("preparing to fire alarm for reminding clients");
        Instant schedTime = Instant.from(event.date).minus(15, ChronoUnit.MINUTES);
        sendNotification(event.eventID, schedTime);
    }

    @Override
    public void sendMailBefore30Min(User user, Event event) throws Exception {

        logger.info("preparing to fire alarm for reminding clients");
        Instant schedTime = Instant.from(event.date).minus(30, ChronoUnit.MINUTES);
        sendNotification(event.eventID, schedTime);
    }

    @Override
    public void sendMailBefore1Hour(User user, Event event) throws Exception {

        logger.info("preparing to fire alarm for reminding clients");
        Instant schedTime = Instant.from(event.date).minus(1, ChronoUnit.HOURS);
        sendNotification(event.eventID, schedTime);
    }

    /**
     *
     * <p> We use this function to help for sending mail with specific time, since this function is quite simple for sending mail to person 
     *     using event ID to pass into function then the function itself will send to people with gmail address </p>
     * 
     * @param eid event identification
     * @return void
     * @throws none
     */
    @Override
    public void sendMail(int eid) {

        try {
            Event e = repo.findEventByID(eid);
            
            logger.info("----------Initializing-----------");

            Scheduler scheduler = getScheduler();

            logger.info("----------Initialization Complete-------------");

            logger.info("----------Scheduling jobs------------");

            JobDetail jobDetail = buildJobDetail(eid);
            Cron cron = CronBuilder(e.date);
            Trigger trigger = buildJobTrigger(jobDetail, cron);

            scheduler.scheduleJob(jobDetail, trigger);

            scheduler.start();

            logger.info("--------Started scheduler----------");

            try {
                // run for 292 billion years
                Thread.sleep(Long.MAX_VALUE);
                // executing...
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }

            logger.info("--------Shutting down--------------");

            scheduler.shutdown(true); 

            logger.info("--------Shutdown complete-----------");

        } catch (SchedulerException e) {
            logger.error("Scheduler throw exception", e);
        } catch (Throwable e) {
            logger.error("Fails", e);
        }
        
    
        
    }
}
