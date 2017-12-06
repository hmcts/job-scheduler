package uk.gov.hmcts.reform.jobscheduler.services.jobs.jobsservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;


@RunWith(MockitoJUnitRunner.class)
public class DeleteJobTest {

    @Mock private Scheduler scheduler;
    @Mock private ActionSerializer actionSerializer;

    @Test
    public void should_throw_an_exception_if_scheduler_fails() throws Exception {
        // given
        String id = "abc123";
        String serviceName = "my service name";

        given(scheduler.deleteJob(any())).willThrow(SchedulerException.class);
        JobsService jobsService = new JobsService(scheduler, actionSerializer);

        // when
        Throwable exc = catchThrowable(() -> jobsService.delete(id, serviceName));

        // then
        assertThat(exc)
            .isInstanceOf(JobException.class)
            .hasMessageContaining(serviceName)
            .hasMessageContaining(id);
    }
}
