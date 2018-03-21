package uk.gov.hmcts.reform.jobscheduler.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class GetWelcomeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobsService service; // NOPMD we only need context to load

    @MockBean
    private AuthTokenValidator tokenValidator; // NOPMD we only need context to load

    @Test
    public void should_welcome_upon_root_request_with_200_response_code() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to Job Scheduler"));
    }
}
