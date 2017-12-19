package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.model.PageRequest;
import uk.gov.hmcts.reform.jobscheduler.model.Pages;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger.Frequency;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.gov.hmcts.reform.jobscheduler.services.jobs.GetterFromScheduler.getFromScheduler;

@Service
public class JobsService {

    private final Scheduler scheduler;
    private final ActionSerializer serializer;

    public JobsService(Scheduler scheduler, ActionSerializer serializer) {
        this.scheduler = scheduler;
        this.serializer = serializer;
    }

    public String create(Job job, String serviceName) {
        try {
            String id = UUID.randomUUID().toString();
            TriggerBuilder<org.quartz.Trigger> triggerBuilder = newTrigger()
                .startAt(Date.from(job.trigger.startDateTime.toInstant()));

            if (null != job.trigger.interval && null != job.trigger.frequency) {
                triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                    .calendarIntervalSchedule()
                    .withInterval(
                        job.trigger.interval,
                        IntervalUnit.valueOf(job.trigger.frequency.name())
                    )
                );
            }

            scheduler.scheduleJob(
                newJob(HttpCallJob.class)
                    .withIdentity(id, serviceName)
                    .withDescription(job.name)
                    .usingJobData(JobDataKeys.PARAMS, serializer.serialize(job.action))
                    .usingJobData(JobDataKeys.ATTEMPT, 1)
                    .requestRecovery()
                    .build(),
                triggerBuilder.build()
            );

            return id;

        } catch (SchedulerException exc) {
            throw new JobException("Error while scheduling a job", exc);
        }
    }

    public void delete(String id, String serviceName) {
        try {
            boolean jobFound = scheduler.deleteJob(JobKey.jobKey(id, serviceName));
            if (!jobFound) {
                throw new JobNotFoundException();
            }
        } catch (SchedulerException exc) {
            throw new JobException(
                "Error while deleting job. ID: " + id + " group: " + serviceName,
                exc
            );
        }
    }

    public Page<JobData> getAll(String serviceName, int page, int size) {
        Set<JobKey> jobKeys = getFromScheduler(scheduler::getJobKeys, GroupMatcher.jobGroupEquals(serviceName));

        int total = jobKeys.size();

        List<JobData> jobs = jobKeys
            .stream()
            .skip(page * size)
            .limit(size)
            .map(jobKey -> new JobData(
                jobKey.getName(),
                getJobFromDetail(getFromScheduler(scheduler::getJobDetail, jobKey))
            ))
            .collect(Collectors.toList());

        return new Pages<>(jobs, PageRequest.of(page, size), total);
    }

    private Job getJobFromDetail(JobDetail jobDetail) {
        Trigger trigger = getFromScheduler(scheduler::getTriggersOfJob, jobDetail.getKey())
            .stream()
            .findFirst()
            .map(quartzTrigger -> {
                Frequency frequency = null;
                Integer interval = null;

                // otherwise it is a SimpleTrigger and only has default interval defined in library
                if (quartzTrigger instanceof CalendarIntervalTrigger) {
                    CalendarIntervalTrigger calendarTrigger = (CalendarIntervalTrigger) quartzTrigger;

                    frequency = Frequency.valueOf(calendarTrigger.getRepeatIntervalUnit().name());
                    interval = calendarTrigger.getRepeatInterval();
                }

                return new Trigger(
                    frequency,
                    interval,
                    ZonedDateTime.ofInstant(quartzTrigger.getStartTime().toInstant(), ZoneId.systemDefault())
                );
            })
            .orElse(null);

        return new Job(
            jobDetail.getDescription(),
            serializer.deserialize(jobDetail.getJobDataMap().getString(JobDataKeys.PARAMS)),
            trigger
        );
    }
}
