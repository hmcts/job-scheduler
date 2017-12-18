package uk.gov.hmcts.reform.jobscheduler.controllers.jobscontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthException;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthService;
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
    @MockBean private AuthService authService;

    @Test
    public void should_return_no_data_when_empty_job_list_is_returned() {
        ResultMatcher contentIsArray = jsonPath("content").isArray();
        ResultMatcher contentIsEmpty = jsonPath("content").isEmpty();
        ResultMatcher pageIsLast = jsonPath("last").value(true);
        ResultMatcher pageIsFirst = jsonPath("first").value(true);

        when(jobsService.getAll(anyString(), anyInt(), anyInt()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        Arrays.asList(
            "/jobs",
            "/jobs?page=1",
            "/jobs?size",
            "/jobs?page=1&size=1"
        ).forEach(url -> {
            try {
                sendGet(url)
                    .andExpect(status().isOk())
                    .andExpect(contentIsArray)
                    .andExpect(contentIsEmpty)
                    .andExpect(pageIsLast)
                    .andExpect(pageIsFirst);
            } catch (Exception exc) {
                fail("Failed to test '" + url + "'", exc);
            }
        });
    }

    @Test
    public void should_return_default_pages_when_no_parameters_are_passed() throws Exception {
        JobData jobData = JobData.fromJob("some-id", validJob());
        when(jobsService.getAll(anyString(), anyInt(), anyInt()))
            .thenReturn(new PageImpl<>(Collections.singletonList(jobData)));

        sendGet()
            .andExpect(status().isOk())
            .andExpect(jsonPath("content[0].action").exists())
            .andExpect(jsonPath("content[0].id").value(jobData.id))
            .andExpect(jsonPath("content[0].name").value(jobData.name))
            .andExpect(jsonPath("totalPages").value(1))
            .andExpect(jsonPath("totalElements").value(1))
            .andExpect(jsonPath("numberOfElements").value(1));
    }

    @Test
    public void should_paginate_response_content_correctly_according_to_parameters() throws Exception {
        // given we have 3 jobs in total

        getResponse(1, 10)
            .andExpect(jsonPath("content").isEmpty())
            .andExpect(jsonPath("totalPages").value(1))
            .andExpect(jsonPath("numberOfElements").value(0));
        getResponse(0, 2)
            .andExpect(jsonPath("totalPages").value(2))
            .andExpect(jsonPath("numberOfElements").value(2));
        getResponse(1, 2)
            .andExpect(jsonPath("totalPages").value(2))
            .andExpect(jsonPath("numberOfElements").value(1));
        getResponse(1, 1)
            .andExpect(jsonPath("totalPages").value(3))
            .andExpect(jsonPath("numberOfElements").value(1));
    }

    @Test
    public void should_return_401_when_auth_token_is_invalid() throws Exception {
        given(authService.authenticate(anyString())).willThrow(new AuthException(null, null));

        sendGet().andExpect(status().isUnauthorized());
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
            jobs = Collections.nCopies(Math.min(copies, size), JobData.fromJob("some-id", validJob()));
        }

        Page<JobData> pages = new PageImpl<>(jobs, PageRequest.of(page, size), total);
        when(jobsService.getAll(anyString(), anyInt(), anyInt())).thenReturn(pages);

        return sendGet("/jobs?page=" + page + "&size=" + size);
    }
}
