
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.util.Arrays;

import Utilities.EventReminderJob;
import Utilities.QuartzReminder;
import entities.Event;
import entities.User;
import repositories.DataRepository;

import java.lang.management.ManagementFactory;
// import java.lang.management.OperatingSystemMXBean;

// @RunWith(EasyMockRunner.class)
public class NormalTest {
    
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
    private User testUser;
    private Event testEvent;

    @BeforeEach
    public void setUp() {
        testUser = new User("testname", "testemail");
        testEvent = new Event(1, "test event", "test organizer", Instant.now(), 1, Arrays.asList("name1","name2","name3"));
    }

    @Test
    public void checkSetUp() {
        User user = mock(User.class);
    }

    @Test 
    public void instantiateScheduler() {
        System.out.println("haha");
    }
    }
