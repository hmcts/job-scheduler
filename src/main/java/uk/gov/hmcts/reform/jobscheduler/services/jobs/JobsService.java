package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.JobList;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobNotFoundException;

import java.util.Date;
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
            job.setId(UUID.randomUUID().toString());

            scheduler.scheduleJob(
                newJob(HttpCallJob.class)
                    .withIdentity(job.getId(), serviceName)
                    .withDescription(job.name)
                    .usingJobData(HttpCallJob.PARAMS_KEY, serializer.serialize(job.action))
                    .requestRecovery()
                    .build(),
                newTrigger()
                    .startAt(Date.from(job.trigger.startDateTime.toInstant()))
                    .build()
            );

            return job.getId();

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

    public JobList getAll(String serviceName) {
        Set<JobKey> jobKeys = getFromScheduler(scheduler::getJobKeys, GroupMatcher.jobGroupEquals(serviceName));

        return new JobList(jobKeys
            .stream()
            .map(jobKey -> Job.fromJobDetail(
                getFromScheduler(scheduler::getJobDetail, jobKey),
                serializer
            ))
            .collect(Collectors.toList())
        );
    }
}
