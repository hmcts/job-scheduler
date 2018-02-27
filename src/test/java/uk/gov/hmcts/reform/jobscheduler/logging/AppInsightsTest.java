package uk.gov.hmcts.reform.jobscheduler.logging;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class AppInsightsTest {

    private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

    @Mock
    private TelemetryClient client;

    private final TelemetryContext context = new TelemetryContext();

    private AppInsights insights;

    @Before
    public void setUp() {
        context.setInstrumentationKey("some-key");

        given(client.getContext()).willReturn(context);

        insights = new AppInsights(client, MAX_NUMBER_OF_ATTEMPTS);
    }

    @Test
    public void should_track_http_job_execution_success_and_failure() {
        insights.trackHttpCallJobExecution(java.time.Duration.ofMillis(1_000), true);
        insights.trackHttpCallJobExecution(java.time.Duration.ofMillis(1_000), false);

        verify(client, times(2)).trackDependency(
            eq(AppInsights.HTTP_CALL_DEPENDENCY),
            eq(AppInsights.HTTP_CALL_EXECUTE_COMMAND),
            any(Duration.class),
            anyBoolean()
        );
    }

    @Test
    public void should_track_job_details_for_success_and_failure() {
        insights.trackJobDetails("job-id", 1, "url", HttpMethod.GET, "body", true);
        insights.trackJobDetails("job-id", 1, "url", HttpMethod.GET, "body", false);

        verify(client, times(2)).trackMetric(
            eq(AppInsights.HTTP_CALL_JOB_DETAIL_METRIC),
            eq((double) 4),
            eq(1),
            eq(1.0),
            eq((double) MAX_NUMBER_OF_ATTEMPTS),
            anyMapOf(String.class, String.class)
        );
    }
}
