package uk.gov.hmcts.reform.jobscheduler.jobs;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.quartz.JobBuilder.newJob;

@RunWith(SpringRunner.class)
public class HttpCallJobTest {

    private static final String TEST_PATH = "/hello-world";
    private static final String TEST_BODY = "some-body";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String X_CUSTOM_HEADER = "X-Custom-Header";
    private static final String NEW_S2S_TOKEN = "newly-generated-token";
    private static final String OLD_S2S_TOKEN = "some-token";
    private static final String CUSTOM_VALUE = "anything";
    private static final String JOB_ID = "jobId123";

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(options().dynamicHttpsPort());

    @Rule
    public WireMockClassRule wireMockRule = wireMockClassRule;

    private final RestTemplate restTemplate = new RestTemplate();

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
            .willReturn(newJob(HttpCallJob.class).withIdentity(JOB_ID, "group").build());

        given(authTokenGenerator.generate()).willReturn(NEW_S2S_TOKEN);
        given(actionExtractor.extract(any())).willReturn(createSampleAction());
    }

    @Test
    public void execute_calls_given_endpoint_url() throws Exception {
        // given
        actionHadHeadersSetTo(Collections.emptyMap());

        // when
        executingHttpCallJob();

        // then
        verify(postRequestedFor(urlEqualTo(TEST_PATH)));
    }

    @Test
    public void execute_calls_endpoint_with_given_body() throws Exception {
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
    public void execute_adds_service_authorization_header() throws Exception {
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
    public void execute_replaces_existing_service_authorization_header() throws Exception {
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
    public void execute_preserves_non_service_authorization_headers() throws Exception {
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

    @Test
    public void execute_should_fail_for_client_error_response() {
        assertExecuteFailsForResponseStatus(401);
    }

    @Test
    public void execute_should_fail_for_server_error_response() {
        assertExecuteFailsForResponseStatus(503);
    }

    @Test
    public void execute_should_fail_when_rest_client_fails() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        given(
            mockRestTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
            )
        ).willThrow(new RestClientException("test exception"));

        actionHadHeadersSetTo(ImmutableMap.of("header", "value"));

        HttpCallJob job = new HttpCallJob(mockRestTemplate, actionExtractor, authTokenGenerator);

        assertThatThrownBy(
            () -> job.execute(context)
        ).isInstanceOf(
            JobExecutionException.class
        ).hasMessage(
            String.format("Job failed. Job ID: %s", JOB_ID)
        ).hasCauseInstanceOf(RestClientException.class);
    }

    private void assertExecuteFailsForResponseStatus(int responseStatus) {
        stubFor(
            post(anyUrl()).willReturn(aResponse().withStatus(responseStatus))
        );

        actionHadHeadersSetTo(ImmutableMap.of("header", "value"));

        assertThatThrownBy(
            () -> executingHttpCallJob()
        ).isInstanceOf(
            JobExecutionException.class
        ).hasMessage(
            String.format("Job failed. Job ID: %s", JOB_ID)
        ).hasCauseInstanceOf(HttpStatusCodeException.class);
    }

    private void actionHadHeadersSetTo(Map<String, String> headers) {
        given(actionExtractor.extract(context))
            .willReturn(createSampleAction(headers));
    }

    private HttpAction createSampleAction() {
        return createSampleAction(ImmutableMap.of("sample-header", "value"));
    }

    private HttpAction createSampleAction(Map<String, String> headers) {
        return new HttpAction(
            "http://localhost:8080" + TEST_PATH,
            HttpMethod.POST,
            headers,
            TEST_BODY
        );
    }

    private void executingHttpCallJob() throws JobExecutionException {
        new HttpCallJob(restTemplate, actionExtractor, authTokenGenerator).execute(context);
    }
}
