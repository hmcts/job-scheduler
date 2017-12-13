package uk.gov.hmcts.reform.jobscheduler.jobs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
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
import uk.gov.hmcts.reform.jobscheduler.config.ApplicationConfiguration;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class HttpCallJobTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Autowired RestTemplate restTemplate;

    @Mock private ActionExtractor actionExtractor;
    @Mock private JobExecutionContext context;

    @Test
    public void execute_should_send_http_request_specified_in_job_details() {

        // given
        stubFor(
            post(urlEqualTo("/hello-world"))
                .willReturn(aResponse().withStatus(200))
        );

        given(actionExtractor.extract(context))
            .willReturn(new HttpAction(
                "http://localhost:8080/hello-world",
                HttpMethod.POST,
                ImmutableMap.of("ServiceAuthorization", "some-token"),
                "some-body"
            ));

        // when
        new HttpCallJob(restTemplate, actionExtractor).execute(context);

        // then
        verify(
            postRequestedFor(urlEqualTo("/hello-world"))
                .withHeader("ServiceAuthorization", equalTo("some-token"))
                .withRequestBody(equalTo("some-body"))
        );
    }
}
