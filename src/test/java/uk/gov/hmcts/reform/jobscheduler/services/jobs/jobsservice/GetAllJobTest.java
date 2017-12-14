package uk.gov.hmcts.reform.jobscheduler.services.jobs.jobsservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.model.JobList;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJob;

@RunWith(MockitoJUnitRunner.class)
public class GetAllJobTest {

    @Mock private Scheduler scheduler;
    @Mock private ActionSerializer actionSerializer;

    private JobsService jobsService;

    @Before
    public void setUp() {
        jobsService = new JobsService(scheduler, actionSerializer);
    }

    @Test
    public void should_return_empty_list_when_there_are_no_scheduled_jobs() throws SchedulerException {
        when(scheduler.getJobKeys(any())).thenReturn(Collections.emptySet());

        JobList jobs = jobsService.getAll("service");

        assertThat(jobs.data).isEmpty();
        verify(scheduler, never()).getJobDetail(any());
    }

    @Test
    public void should_return_a_list_of_1_job() throws SchedulerException {
        JobKey jobKey = new JobKey("name", "group");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(HttpCallJob.PARAMS_KEY, "value");

        JobDetail jobDetail = JobBuilder
            .newJob(HttpCallJob.class)
            .withIdentity(jobKey)
            .withDescription("description")
            .usingJobData(jobDataMap)
            .build();

        HttpAction action = validJob().action;

        when(scheduler.getJobKeys(any())).thenReturn(Collections.singleton(jobKey));
        when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
        when(actionSerializer.deserialize("value")).thenReturn(action);

        JobList jobs = jobsService.getAll("service");

        assertThat(jobs.data).extracting("job.action", HttpAction.class).containsOnlyOnce(action);
        assertThat(jobs.data).extracting("id", String.class).containsOnlyOnce("name");
    }
}
