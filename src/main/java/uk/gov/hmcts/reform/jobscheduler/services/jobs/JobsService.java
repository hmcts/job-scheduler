package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;

import java.util.Date;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

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
                    .usingJobData(HttpCallJob.PARAMS_KEY, serializer.serialize(job.action))
                    .build(),
                newTrigger()
                    .startAt(Date.from(job.trigger.startDateTime.toInstant()))
                    .build()
            );
            return id;

        } catch (SchedulerException exc) {
            throw new JobException("Error while scheduling a job", exc);
        }
    }

}
