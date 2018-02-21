package uk.gov.hmcts.reform.jobscheduler.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void should_return_empty_list_of_jobs() throws Exception {
        send("")
            .andExpect(status().isOk())
            .andExpect(content().json(
                "{"
                    + "\"content\":[],"
                    + "\"total_pages\":0,"
                    + "\"total_elements\":0,"
                    + "\"last\":true,"
                    + "\"size\":10,"
                    + "\"number\":0,"
                    + "\"sort\":null,"
                    + "\"first\":true,"
                    + "\"number_of_elements\":0"
                + "}"
            ))
            .andReturn();
    }

    private ResultActions send(String content) throws Exception {
        MockHttpServletRequestBuilder request =
            get("/jobs")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        return mvc.perform(request);
    }
}
