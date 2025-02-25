package com.sequenceiq.cloudbreak.job.nodestatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.inject.Inject;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.quartz.JobSchedulerService;
import com.sequenceiq.cloudbreak.quartz.configuration.TransactionalScheduler;
import com.sequenceiq.cloudbreak.quartz.model.JobResourceAdapter;
import com.sequenceiq.cloudbreak.util.RandomUtil;

@Service
public class NodeStatusCheckerJobService implements JobSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeStatusCheckerJobService.class);

    private static final String JOB_GROUP = "nodestatus-checker-jobs";

    private static final String TRIGGER_GROUP = "nodestatus-checker-triggers";

    @Inject
    private NodeStatusCheckerConfig properties;

    @Inject
    private TransactionalScheduler scheduler;

    @Inject
    private ApplicationContext applicationContext;

    public <T> void schedule(JobResourceAdapter<T> resource) {
        JobDetail jobDetail = buildJobDetail(resource);
        Trigger trigger = buildJobTrigger(jobDetail, RandomUtil.getInt(properties.getIntervalInSeconds()));
        schedule(jobDetail, trigger, resource.getJobResource().getLocalId());
    }

    public <T> void schedule(JobResourceAdapter<T> resource, int delayInSeconds) {
        JobDetail jobDetail = buildJobDetail(resource);
        Trigger trigger = buildJobTrigger(jobDetail, delayInSeconds);
        schedule(jobDetail, trigger, resource.getJobResource().getLocalId());
    }

    private void schedule(JobDetail jobDetail, Trigger trigger, String localId) {
        try {
            JobKey jobKey = JobKey.jobKey(localId, JOB_GROUP);
            if (scheduler.getJobDetail(jobKey) != null) {
                LOGGER.info("Unscheduling node status checker job for stack with key: '{}' and group: '{}'", jobKey.getName(), jobKey.getGroup());
                unschedule(localId);
            }
            LOGGER.info("Scheduling node status checker job for stack with key: '{}' and group: '{}'", jobKey.getName(), jobKey.getGroup());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            LOGGER.error(String.format("Error during scheduling quartz job: %s", localId), e);
        }
    }

    public void schedule(Long id, Class<? extends JobResourceAdapter> resource) {
        try {
            Constructor<? extends JobResourceAdapter> c = resource.getConstructor(Long.class, ApplicationContext.class);
            JobResourceAdapter resourceAdapter = c.newInstance(id, applicationContext);
            schedule(resourceAdapter);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOGGER.error(String.format("Error during scheduling quartz job: %s", id), e);
        }
    }

    public void unschedule(String id) {
        try {
            JobKey jobKey = JobKey.jobKey(id, JOB_GROUP);
            LOGGER.info("Unscheduling node status checker job for stack with key: '{}' and group: '{}'", jobKey.getName(), jobKey.getGroup());
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            LOGGER.error(String.format("Error during unscheduling quartz job: %s", id), e);
        }
    }

    private <T> JobDetail buildJobDetail(JobResourceAdapter<T> resource) {
        JobDataMap jobDataMap = resource.toJobDataMap();

        return JobBuilder.newJob(resource.getJobClassForResource())
                .withIdentity(resource.getJobResource().getLocalId(), JOB_GROUP)
                .withDescription("Checking stack nodestatus Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, int delayInSeconds) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .usingJobData(jobDetail.getJobDataMap())
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP)
                .withDescription("Checking nodestatus Trigger")
                .startAt(delayedStart(delayInSeconds))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(properties.getIntervalInSeconds())
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }

    private Date delayedStart(int delayInSeconds) {
        return Date.from(ZonedDateTime.now().toInstant().plus(Duration.ofSeconds(delayInSeconds)));
    }

    @Override
    public String getJobGroup() {
        return JOB_GROUP;
    }
}
