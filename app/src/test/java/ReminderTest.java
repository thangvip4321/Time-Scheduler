
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;



import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;


import entities.Event;
import entities.User;
import reminder.EventReminderJob;
import reminder.QuartzReminder;
import repositories.DataRepository;

import java.lang.management.ManagementFactory;
// import java.lang.management.OperatingSystemMXBean;

// @RunWith(EasyMockRunner.class)
public class ReminderTest {
    
    /**
     * Heading: Test QuartzReminder,java
     * * check the functionality of method 'sendMailBefore5Min'
     * 1. sender must be organizer
     * 2. will the progress move to the execute method in EventReminderJob.java
     * 2.1 mock database
     * 3. send mail the one who receive mail must be recipients
     * 
     * 4. create a loop for 100 times to instantiate scheduler by using method getScheduler()
     * 4.1 compare the getScheduler() method vs instantiate scheduler from the field of the class QuartzReminder
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderTest.class);
    private User testUser;
    private Event testEvent;
    // @Mock annotation is used to create the mock object to be injected
    @Mock DataRepository repository;


    @BeforeEach
    public void setUp() {
        testUser = new User("testname", "testemail");
        // new Event(eventID, eventName, organizer, startTime, endTime, location, priority, participantsList, remindBefore)
        testEvent = new Event(1, "test event", "test organizer", Instant.now().plus(310, ChronoUnit.SECONDS),Instant.now().plus(10, ChronoUnit.HOURS),"my house", "Low", Arrays.asList("name1","name2","name3"));
        repository = EasyMock.mock(DataRepository.class);
        for (int i = 0; i < 10; i++) {
            testEvent.eventID = i;
            EasyMock.expect(repository.findEventByID(testEvent.eventID)).andReturn(testEvent);
            for (String username : testEvent.participantsList) {
                EasyMock.expect(repository.findUserByName(username)).andReturn(new User(username, "timescheduler180@gmail.com"));
            }
        }
        EasyMock.replay(repository);
    }



    // @TestSubject annotation is used to identify class which is going to use the mock object
    @TestSubject private EventReminderJob eventReminderJob = new EventReminderJob();

    @Test
    public void checkSetUp() {
        // assertEquals("2", "2");
        // int a = 2;
        // User user = mock(User.class);
        // assertEquals(user.email, testUser.email); 
        // assertEquals(user.userID, testUser.userID);
    }

    @Test 
    public void instantiateScheduler() {

    }

    @Test
    public void testEventReminder() throws Exception {
        // when(repository.)
        var reminder = new QuartzReminder(repository);
        reminder.sendMailBefore5Min(testEvent);
        
    }


    @Test
    public void testSpam() throws Exception {
        var memoryTest = ManagementFactory.getMemoryMXBean();
        var cpuTest = ManagementFactory.getThreadMXBean();
        LOGGER.error("# of threads {}",cpuTest.getAllThreadIds());
        LOGGER.error("memory before: {}", (Object)memoryTest.getHeapMemoryUsage().getUsed());
        var reminder = new QuartzReminder(repository);
        for (int i = 0; i < 10; i++) {
            testEvent.eventID = i;
            reminder.sendMailBefore5Min(testEvent);      
        }
        Thread.sleep(15000);

        EasyMock.verifyRecording(repository);
    }
}
