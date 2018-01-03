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
import org.springframework.data.domain.Page;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobDataKeys;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJob;
import static uk.gov.hmcts.reform.jobscheduler.services.jobs.SchedulerExceptionHandlingHelper.call;

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

        Page<JobData> pages = jobsService.getAll("service", 1, 1);

        assertThat(pages.getTotalElements()).isEqualTo(0);
        verify(scheduler, never()).getJobDetail(any());
    }

    @Test
    public void should_return_a_list_of_jobs_correctly_paginated() throws SchedulerException {
        Page<JobData> pages1 = setUpAndRetrieve(1, 0, 10);

        assertThat(pages1.getTotalElements()).isEqualTo(1);
        assertThat(pages1.getTotalPages()).isEqualTo(1);
        assertThat(pages1.getNumberOfElements()).isEqualTo(1);
        assertThat(pages1.getContent()).extracting("job.action", HttpAction.class).isNotEmpty();
        assertThat(pages1.getContent()).extracting("id", String.class).containsOnlyOnce("name1");

        Page<JobData> pages2 = setUpAndRetrieve(3, 1, 10);

        assertThat(pages2.getTotalElements()).isEqualTo(3);
        assertThat(pages2.getTotalPages()).isEqualTo(1);
        assertThat(pages2.getNumberOfElements()).isEqualTo(0);
        assertThat(pages2.getContent()).isEmpty();

        Page<JobData> pages3 = setUpAndRetrieve(3, 0, 2);

        assertThat(pages3.getTotalPages()).isEqualTo(2);
        assertThat(pages3.getNumberOfElements()).isEqualTo(2);

        Page<JobData> pages4 = setUpAndRetrieve(3, 1, 2);

        assertThat(pages4.getTotalPages()).isEqualTo(2);
        assertThat(pages4.getNumberOfElements()).isEqualTo(1);

        Page<JobData> pages5 = setUpAndRetrieve(3, 1, 1);

        assertThat(pages5.getTotalPages()).isEqualTo(3);
        assertThat(pages5.getNumberOfElements()).isEqualTo(1);
    }

    private Page<JobData> setUpAndRetrieve(int total, int page, int size) throws SchedulerException {
        Set<JobKey> keys;

        if (total < 1) {
            keys = Collections.emptySet();
        } else {
            keys = IntStream.rangeClosed(1, total)
                .mapToObj(it -> {
                    JobKey jobKey = new JobKey("name" + it, "group");
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put(JobDataKeys.PARAMS, "value");
                    JobDetail jobDetail = JobBuilder.newJob(HttpCallJob.class)
                        .withIdentity(jobKey)
                        .withDescription("description")
                        .usingJobData(jobDataMap)
                        .build();

                    when(call(() -> scheduler.getJobDetail(jobKey))).thenReturn(jobDetail);

                    return jobKey;
                })
                .collect(Collectors.toSet());
        }

        when(scheduler.getJobKeys(any())).thenReturn(keys);
        when(actionSerializer.deserialize("value")).thenReturn(validJob().action);

        return jobsService.getAll("service", page, size);
    }
}
