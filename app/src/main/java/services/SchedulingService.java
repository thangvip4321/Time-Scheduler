package services;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public interface SchedulingService {

    void startScheduler() throws SchedulerException;

    void standbyScheduler() throws SchedulerException;

    void shutdownScheduler() throws SchedulerException;

    void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException;
}