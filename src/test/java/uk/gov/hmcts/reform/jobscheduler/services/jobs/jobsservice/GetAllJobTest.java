package uk.gov.hmcts.reform.jobscheduler.services.jobs.jobsservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
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
    public void should_return_empty_list() throws SchedulerException {
        when(scheduler.getJobKeys(any())).thenReturn(Collections.emptySet());

        List<Job> jobs = jobsService.getAll("service");

        assertThat(jobs).isEmpty();
        verify(scheduler, never()).getJobDetail(any());
    }

    @Test
    public void should_return_a_list_of_1_job() throws SchedulerException {
        HttpAction action = validJob().action;
        JobKey jobKey = new JobKey("name", "group");
        JobDetail jobDetail = mock(JobDetail.class);
        JobDataMap jobDataMap = mock(JobDataMap.class);

        when(scheduler.getJobKeys(any())).thenReturn(Collections.singleton(jobKey));
        when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
        when(jobDetail.getDescription()).thenReturn("description");
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.getString(HttpCallJob.PARAMS_KEY)).thenReturn("value");
        when(actionSerializer.deserialize("value")).thenReturn(action);

        List<Job> jobs = jobsService.getAll("service");

        assertThat(jobs).extracting("action", HttpAction.class).contains(action);
    }
}
