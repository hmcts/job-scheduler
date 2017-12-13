package uk.gov.hmcts.reform.jobscheduler.services.jobs.jobsservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.Scheduler;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void should_get_all() throws Exception {
        List<Job> jobs = jobsService.getAll("service");

        assertThat(jobs).isEmpty();
    }

}
