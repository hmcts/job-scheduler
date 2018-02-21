package uk.gov.hmcts.reform.jobscheduler.controllers.jobscontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import uk.gov.hmcts.reform.jobscheduler.FixtureData;
import uk.gov.hmcts.reform.jobscheduler.controllers.JobsController;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.model.JobSchedulerPageRequest;
import uk.gov.hmcts.reform.jobscheduler.model.Pages;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJob;

@RunWith(SpringRunner.class)
@WebMvcTest
public class GetAllTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private JobsService jobsService;
    @MockBean private ServiceAuthTokenValidator validator;

    @Test
    public void should_return_no_data_when_empty_job_list_is_returned() {
        when(jobsService.getAll(anyString(), anyInt(), anyInt()))
            .thenReturn(new Pages<>(Collections.emptyList()));

        Arrays.asList(
            "/jobs",
            "/jobs?page=1",
            "/jobs?size=1",
            "/jobs?page=1&size=1"
        ).forEach(url -> {
            try {
                sendGet(url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("content").isArray())
                    .andExpect(jsonPath("content").isEmpty())
                    .andExpect(jsonPath("last").value(true))
                    .andExpect(jsonPath("first").value(true));
            } catch (Exception exc) {
                fail("Failed to test '" + url + "'", exc);
            }
        });
    }

    @Test
    public void should_return_default_pages_when_no_parameters_are_passed() throws Exception {
        JobData jobData = new JobData("some-id", validJob());
        when(jobsService.getAll(anyString(), anyInt(), anyInt()))
            .thenReturn(new Pages<>(Collections.singletonList(jobData)));

        sendGet()
            .andExpect(status().isOk())
            .andExpect(jsonPath("content[0].action").exists())
            .andExpect(jsonPath("content[0].id").value(jobData.id))
            .andExpect(jsonPath("content[0].name").value(jobData.job.name))
            .andExpect(jsonPath("total_pages").value(1))
            .andExpect(jsonPath("total_elements").value(1))
            .andExpect(jsonPath("number_of_elements").value(1));
    }

    @Test
    public void should_paginate_response_content_correctly_according_to_parameters() throws Exception {
        // given we have 3 jobs in total

        getResponse(1, 10)
            .andExpect(jsonPath("content").isEmpty())
            .andExpect(jsonPath("total_pages").value(1))
            .andExpect(jsonPath("number_of_elements").value(0));
        getResponse(0, 2)
            .andExpect(jsonPath("total_pages").value(2))
            .andExpect(jsonPath("number_of_elements").value(2));
        getResponse(1, 2)
            .andExpect(jsonPath("total_pages").value(2))
            .andExpect(jsonPath("number_of_elements").value(1));
        getResponse(1, 1)
            .andExpect(jsonPath("total_pages").value(3))
            .andExpect(jsonPath("number_of_elements").value(1));
    }

    @Test
    public void should_return_401_when_auth_token_is_invalid() throws Exception {
        given(validator.getServiceName(anyString())).willThrow(FixtureData.getAuthorisationException());

        sendGet().andExpect(status().isUnauthorized());
    }

    @Test
    public void should_return_400_when_request_parameters_are_out_of_bounds() throws Exception {
        sendGet("/jobs?page=-1")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("error").value("must be greater than or equal to 0"));

        int minSize = JobsController.MIN_PAGE_SIZE - 1;

        sendGet("/jobs?size=" + minSize)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("error")
                .value("must be greater than or equal to " + JobsController.MIN_PAGE_SIZE)
            );

        int maxSize = JobsController.MAX_PAGE_SIZE + 1;

        sendGet("/jobs?size=" + maxSize)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("error")
                .value("must be less than or equal to " + JobsController.MAX_PAGE_SIZE)
            );
    }

    private ResultActions sendGet(String url) throws Exception {
        return mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON)
            .header("ServiceAuthorization", "some-service-auth-header")
        );
    }

    private ResultActions sendGet() throws Exception {
        return sendGet("/jobs");
    }

    private ResultActions getResponse(int page, int size) throws Exception {
        int total = 3;
        int skip = page * size;
        int copies = total - skip;
        List<JobData> jobs;

        if (copies < 1) {
            jobs = Collections.emptyList();
        } else {
            jobs = Collections.nCopies(Math.min(copies, size), new JobData("some-id", validJob()));
        }

        Page<JobData> pages = new Pages<>(jobs, JobSchedulerPageRequest.of(page, size), total);
        when(jobsService.getAll(anyString(), anyInt(), anyInt())).thenReturn(pages);

        return sendGet("/jobs?page=" + page + "&size=" + size);
    }
}
