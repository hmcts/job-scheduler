package uk.gov.hmcts.reform.jobscheduler.logging;

import com.google.common.collect.ImmutableMap;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;

@Component
public class AppInsights extends AbstractAppInsights {

    static final String HTTP_CALL_DEPENDENCY = "HttpCallJob";
    static final String HTTP_CALL_EXECUTE_COMMAND = "Execute";
    static final String HTTP_CALL_JOB_DETAIL_METRIC = "JobDetail";

    private final int maxNumberOfAttempts;

    public AppInsights(TelemetryClient telemetry,
                       @Value("${retryPolicy.maxNumberOfJobExecutions}") int maxNumberOfAttempts) {
        super(telemetry);

        this.maxNumberOfAttempts = maxNumberOfAttempts;
    }

    public void trackHttpCallJobExecution(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            HTTP_CALL_DEPENDENCY,
            HTTP_CALL_EXECUTE_COMMAND,
            new Duration(duration.toMillis()),
            success
        );
    }

    public void trackJobDetails(String jobId,
                                int attempt,
                                String url,
                                HttpMethod method,
                                String body,
                                boolean success) {
        telemetry.trackMetric(
            HTTP_CALL_JOB_DETAIL_METRIC,
            body.length(),
            attempt,
            1,
            maxNumberOfAttempts,
            ImmutableMap.of(
                "jobId", jobId,
                "method", method.name(),
                "url", url,
                "success", String.valueOf(success)
            )
        );
    }
}
