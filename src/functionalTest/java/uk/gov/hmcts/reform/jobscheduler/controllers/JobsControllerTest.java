package uk.gov.hmcts.reform.jobscheduler.controllers;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobNotFoundException;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void should_return_empty_list_of_jobs() throws Exception {
        sendGetRequest()
            .andExpect(status().isOk())
            .andExpect(jsonPath("content").isEmpty())
            .andExpect(jsonPath("total_pages").value(0))
            .andExpect(jsonPath("total_elements").value(0))
            .andExpect(jsonPath("last").value(true))
            .andExpect(jsonPath("size").value(10))
            .andExpect(jsonPath("number").value(0))
            .andExpect(jsonPath("sort").value(isNull()))
            .andExpect(jsonPath("first").value(true))
            .andExpect(jsonPath("number_of_elements").value(0));
    }

    @Test
    public void should_create_a_new_job_and_return_job_uri_of_newly_created_job_when_valid_job_input_is_passed()
        throws Exception {
        //Given
        String jobJson = jobJson();

        //when
        MvcResult mvcResult = sendPostRequest(jobJson)
            .andExpect(status().isCreated())
            .andReturn();

        //then
        URI newJobUri = new URI(mvcResult.getResponse().getRedirectedUrl());

        assertThat(newJobUri).isNotNull();

        //Delete the newly created job else it will used by other test cases
        sendDeleteRequest(extractJobId(mvcResult.getResponse().getRedirectedUrl()))
            .andExpect(status().isNoContent());
    }

    @Test
    public void should_not_create_a_new_job_and_throw_4xx_client_error_when_empty_job_input_is_passed()
        throws Exception {
        sendPostRequest("")
            .andExpect(status().is4xxClientError())
            .andReturn();
    }

    @Test
    public void should_delete_job_successfully_when_existing_job_id_is_passed() throws Exception {
        //Given
        String jobJson = jobJson();

        //when
        MvcResult mvcResult = sendPostRequest(jobJson)
            .andExpect(status().isCreated())
            .andReturn();

        //then
        String jobId = extractJobId(mvcResult.getResponse().getRedirectedUrl());

        sendDeleteRequest(jobId)
            .andExpect(status().isNoContent());
    }

    @Test
    public void should_not_delete_job_and_throw_job_not_exception_when_non_existing_job_id_is_passed()
        throws Exception {
        MvcResult mvcResult = sendDeleteRequest(UUID.randomUUID().toString())
            .andExpect(status().is4xxClientError())
            .andReturn();

        assertThat(mvcResult.getResolvedException())
            .isExactlyInstanceOf(JobNotFoundException.class);
    }

    @Test
    public void should_return_default_pages_when_no_parameters_are_passed() throws Exception {
        MvcResult mvcResult = sendPostRequest(jobJson())
            .andExpect(status().isCreated())
            .andReturn();

        String jobId = extractJobId(mvcResult.getResponse().getRedirectedUrl());

        sendGetRequest()
            .andExpect(status().isOk())
            .andExpect(jsonPath("content[0].action").exists())
            .andExpect(jsonPath("content[0].id").value(jobId))
            .andExpect(jsonPath("content[0].name").value("my-job-name"))
            .andExpect(jsonPath("total_pages").value(1))
            .andExpect(jsonPath("total_elements").value(1))
            .andExpect(jsonPath("number_of_elements").value(1));
    }

    private ResultActions sendGetRequest() throws Exception {
        MockHttpServletRequestBuilder request =
            get("/jobs")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON);

        return mvc.perform(request);
    }

    private ResultActions sendPostRequest(String content) throws Exception {
        MockHttpServletRequestBuilder request =
            post("/jobs")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        return mvc.perform(request);
    }

    private ResultActions sendDeleteRequest(String id) throws Exception {
        MockHttpServletRequestBuilder request =
            delete("/jobs/" + id)
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON);

        return mvc.perform(request);
    }

    public static String jobJson() throws Exception {
        return new JSONObject()
            .put("name", "my-job-name")
            .put("action", new JSONObject()
                .put("url", "https://some-service.gov.uk/do-something")
                .put("method", "POST")
                .put("headers", new JSONObject()
                    .put("Authorization", "some-auth-token")
                )
                .put("body", "hello")
            )
            .put("trigger", new JSONObject()
                .put("start_date_time", "2042-08-11T12:11:00Z")
            )
            .toString();
    }

    private String extractJobId(String uri) {
        Path urlPath = Paths.get(uri);
        return urlPath.getName(urlPath.getNameCount() - 1).toString();
    }
}
