package uk.gov.hmcts.reform.jobscheduler.jobs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static java.util.Collections.emptyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class HttpCallJobTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Autowired RestTemplate restTemplate;
    @Mock private ActionExtractor actionExtractor;

    @Test
    public void execute_should_send_http_request_to_url_saved_in_job_details() {

        JobExecutionContext context = mock(JobExecutionContext.class);

        HttpAction actionToPerform =
            new HttpAction(
                "http://localhost:8080/hello-world",
                HttpMethod.POST,
                emptyMap(),
                null
            );

        stubFor(
            post(urlEqualTo("/hello-world"))
                .willReturn(aResponse().withStatus(200))
        );

        given(actionExtractor.extract(context)).willReturn(actionToPerform);

        // when
        HttpCallJob job = new HttpCallJob(restTemplate, actionExtractor);
        job.execute(context);

        // then
    }
}
