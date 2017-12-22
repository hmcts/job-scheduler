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
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import uk.gov.hmcts.reform.jobscheduler.FixtureData;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class DeleteTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private JobsService jobsService; //NOPMD suppress, mock needed
    @MockBean private ServiceAuthTokenValidator validator;

    @Test
    public void should_return_204_when_job_is_deleted() throws Exception {
        sendDelete().andExpect(status().isNoContent());
    }

    @Test
    public void should_return_401_when_auth_token_is_invalid() throws Exception {
        given(validator.getServiceName(anyString())).willThrow(FixtureData.setUpUnauthorisedAccess());

        sendDelete().andExpect(status().isUnauthorized());
    }

    private ResultActions sendDelete() throws Exception {
        return mockMvc.perform(
            delete("/jobs/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .header("ServiceAuthorization", "some-service-auth-header")
        );
    }
}
