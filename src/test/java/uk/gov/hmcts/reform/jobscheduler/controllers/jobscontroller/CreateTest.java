package uk.gov.hmcts.reform.jobscheduler.controllers.jobscontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.jobscheduler.SampleData;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.JobsService;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthException;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthService;
import uk.gov.hmcts.reform.jobscheduler.services.auth.S2sClient;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest
public class CreateTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private JobsService jobsService;
    @MockBean private AuthService authService;

    @Test
    public void should_return_201_when_job_is_created() throws Exception {
        send(SampleData.validJobJson())
            .andExpect(status().isCreated());
    }

    @Test
    public void should_fill_location_header_on_successful_save() throws Exception {
        String id = "1234abcd";
        given(jobsService.create(any(Job.class), anyString()))
            .willReturn(id);

        send(SampleData.validJobJson())
            .andExpect(header().string(LOCATION, endsWith("/jobs/" + id)));
    }

    @Test
    public void should_return_401_when_token_is_invalid() throws Exception {
        given(authService.authenticate(anyString()))
            .willThrow(new AuthException(null, null));

        send(SampleData.validJobJson())
            .andExpect(status().isUnauthorized());
    }

    private ResultActions send(String content) throws Exception {
        return mockMvc.perform(
            post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("ServiceAuthorization", "some-service-auth-header")
                .content(content)
        );
    }
}
