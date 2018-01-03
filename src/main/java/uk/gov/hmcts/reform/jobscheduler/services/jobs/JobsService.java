package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.model.PageRequest;
import uk.gov.hmcts.reform.jobscheduler.model.Pages;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.newJob;
import static uk.gov.hmcts.reform.jobscheduler.services.jobs.SchedulerExceptionHandlingHelper.call;

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

            scheduler.scheduleJob(
                newJob(HttpCallJob.class)
                    .withIdentity(id, serviceName)
                    .withDescription(job.name)
                    .usingJobData(JobDataKeys.PARAMS, serializer.serialize(job.action))
                    .requestRecovery()
                    .build(),
                TriggerConverter.toQuartzTrigger(job.trigger)
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
        Set<JobKey> jobKeys = call(() -> scheduler.getJobKeys(GroupMatcher.jobGroupEquals(serviceName)));

        int total = jobKeys.size();

        List<JobData> jobs = jobKeys
            .stream()
            .skip(page * size)
            .limit(size)
            .map(jobKey -> new JobData(
                jobKey.getName(),
                getJobFromDetail(call(() -> scheduler.getJobDetail(jobKey)))
            ))
            .collect(Collectors.toList());

        return new Pages<>(jobs, PageRequest.of(page, size), total);
    }

    private Job getJobFromDetail(JobDetail jobDetail) {
        Trigger trigger = call(() -> scheduler.getTriggersOfJob(jobDetail.getKey()))
            .stream()
            .filter(quartzTrigger -> quartzTrigger.getJobDataMap().getIntValue(JobDataKeys.ATTEMPT) == 1)
            .findFirst()
            .map(TriggerConverter::toPlatformTrigger)
            .orElse(null);

        return new Job(
            jobDetail.getDescription(),
            serializer.deserialize(jobDetail.getJobDataMap().getString(JobDataKeys.PARAMS)),
            trigger
        );
    }
}
