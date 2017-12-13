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
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthException;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthService;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class GetAllTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private JobsService jobsService;
    @MockBean private AuthService authService;

    @Test
    public void should_return_a_list() throws Exception {
        when(jobsService.getAll(anyString())).thenReturn(Collections.emptyList());

        sendGet()
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
        sendGet("/jobs?page=1")
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
        sendGet("/jobs?size=1")
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
        sendGet("/jobs?page=1&size=1")
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
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
}
