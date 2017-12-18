package uk.gov.hmcts.reform.jobscheduler.jobs;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
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

import java.util.Collections;
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

    private static final String TEST_PATH = "/hello-world";
    private static final String TEST_BODY = "some-body";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String X_CUSTOM_HEADER = "X-Custom-Header";
    private static final String NEW_S2S_TOKEN = "newly-generated-token";
    private static final String OLD_S2S_TOKEN = "some-token";
    private static final String CUSTOM_VALUE = "anything";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Autowired RestTemplate restTemplate;

    @Mock private ActionExtractor actionExtractor;
    @Mock private JobExecutionContext context;
    @Mock private AuthTokenGenerator authTokenGenerator;

    @Before
    public void setup() {
        stubFor(
            post(urlEqualTo(TEST_PATH))
                .willReturn(aResponse().withStatus(200))
        );

        given(context.getJobDetail())
            .willReturn(newJob(HttpCallJob.class).withIdentity("id", "group").build());

        given(authTokenGenerator.generate()).willReturn(NEW_S2S_TOKEN);
    }

    @Test
    public void execute_calls_given_endpoint_url() {
        // given
        actionHadHeadersSetTo(Collections.emptyMap());

        // when
        executingHttpCallJob();

        // then
        verify(postRequestedFor(urlEqualTo(TEST_PATH)));
    }

    @Test
    public void execute_calls_endpoint_with_given_body() {
        // given
        actionHadHeadersSetTo(Collections.emptyMap());

        // when
        executingHttpCallJob();

        // then
        verify(
            postRequestedFor(urlEqualTo(TEST_PATH))
                .withRequestBody(equalTo(TEST_BODY))
        );
    }

    @Test
    public void execute_adds_service_authorization_header() {
        // given
        actionHadHeadersSetTo(Collections.emptyMap());

        // when
        executingHttpCallJob();

        // then
        verify(
            postRequestedFor(urlEqualTo(TEST_PATH))
                .withHeader(SERVICE_AUTHORIZATION_HEADER, equalTo(NEW_S2S_TOKEN))
        );
    }

    @Test
    public void execute_replaces_existing_service_authorization_header() {
        // given
        actionHadHeadersSetTo(ImmutableMap.of(SERVICE_AUTHORIZATION_HEADER, OLD_S2S_TOKEN));

        // when
        executingHttpCallJob();

        // then
        verify(
            postRequestedFor(urlEqualTo(TEST_PATH))
                .withHeader(SERVICE_AUTHORIZATION_HEADER, equalTo(NEW_S2S_TOKEN))
        );
    }

    @Test
    public void execute_preserves_non_service_authorization_headers() {
        // given
        actionHadHeadersSetTo(ImmutableMap.of(X_CUSTOM_HEADER, CUSTOM_VALUE));

        // when
        executingHttpCallJob();

        // then
        verify(
            postRequestedFor(urlEqualTo(TEST_PATH))
                .withHeader(X_CUSTOM_HEADER, equalTo(CUSTOM_VALUE))
        );
    }

    private void actionHadHeadersSetTo(Map<String, String> headers) {
        given(actionExtractor.extract(context))
            .willReturn(new HttpAction(
                "http://localhost:8080" + TEST_PATH,
                HttpMethod.POST,
                headers,
                TEST_BODY
            ));
    }

    private void executingHttpCallJob() {
        new HttpCallJob(restTemplate, actionExtractor, authTokenGenerator).execute(context);
    }
}
