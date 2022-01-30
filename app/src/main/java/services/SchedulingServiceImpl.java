package services;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    @Autowired
    private Scheduler scheduler;    

    @Override
    public void startScheduler() throws SchedulerException {
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    @Override
    public void standbyScheduler() throws SchedulerException {
        if (!scheduler.isInStandbyMode()) {
            scheduler.standby();
        }
    }

    @Override
    public void shutdownScheduler() throws SchedulerException {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    @Override
    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }
}