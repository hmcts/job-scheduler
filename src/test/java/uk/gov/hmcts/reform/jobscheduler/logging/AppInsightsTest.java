package uk.gov.hmcts.reform.jobscheduler.logging;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class AppInsightsTest {

    @Mock
    private TelemetryClient client;

    private TelemetryContext context = new TelemetryContext();

    private AppInsights insights;

    @Before
    public void setUp() {
        context.setInstrumentationKey("some-key");

        given(client.getContext()).willReturn(context);

        insights = new AppInsights(client);
    }

    @Test
    public void should_track_http_job_execution_success() {
        insights.trackHttpCallJobExecution(java.time.Duration.ofMillis(1_000), true);

        verify(client).trackDependency(
            eq(AppInsights.HTTP_CALL_DEPENDENCY),
            eq(AppInsights.HTTP_CALL_EXECUTE_COMMAND),
            any(Duration.class),
            eq(true)
        );
    }

    @Test
    public void should_track_http_job_execution_failure() {
        insights.trackHttpCallJobExecution(java.time.Duration.ofMillis(1_000), false);

        verify(client).trackDependency(
            eq(AppInsights.HTTP_CALL_DEPENDENCY),
            eq(AppInsights.HTTP_CALL_EXECUTE_COMMAND),
            any(Duration.class),
            eq(false)
        );
    }
}
