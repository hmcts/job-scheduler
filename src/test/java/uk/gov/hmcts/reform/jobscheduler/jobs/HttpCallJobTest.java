package uk.gov.hmcts.reform.jobscheduler.jobs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.jobscheduler.config.ApplicationConfiguration;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.BDDMockito.given;
import static org.quartz.JobBuilder.newJob;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class HttpCallJobTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Autowired RestTemplate restTemplate;

    @Mock private ActionExtractor actionExtractor;
    @Mock private JobExecutionContext context;
    @Mock private AuthTokenGenerator authTokenGenerator;

    @Test
    public void execute_should_send_http_request_specified_in_job_details() {

        // given
        stubFor(
            post(urlEqualTo("/hello-world"))
                .willReturn(aResponse().withStatus(200))
        );

        given(context.getJobDetail())
            .willReturn(newJob(HttpCallJob.class).withIdentity("id", "group").build());

        Map<String, String> headers = new HashMap<>();
        headers.put("ServiceAuthorization", "some-token");

        given(actionExtractor.extract(context))
            .willReturn(new HttpAction(
                "http://localhost:8080/hello-world",
                HttpMethod.POST,
                headers,
                "some-body"
            ));

        given(authTokenGenerator.generate()).willReturn("newly-generated-token");
        // when
        new HttpCallJob(restTemplate, actionExtractor, authTokenGenerator).execute(context);

        // then
        verify(
            postRequestedFor(urlEqualTo("/hello-world"))
                .withHeader("ServiceAuthorization", equalTo("newly-generated-token"))
                .withRequestBody(equalTo("some-body"))
        );
    }
}
