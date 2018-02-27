package uk.gov.hmcts.reform.jobscheduler.logging;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;

@Component
public class AppInsights extends AbstractAppInsights {

    static final String HTTP_CALL_DEPENDENCY = "HttpCallJob";
    static final String HTTP_CALL_EXECUTE_COMMAND = "Execute";

    public AppInsights(TelemetryClient telemetry) {
        super(telemetry);
    }

    public void trackHttpCallJobExecution(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            HTTP_CALL_DEPENDENCY,
            HTTP_CALL_EXECUTE_COMMAND,
            new Duration(duration.toMillis()),
            success
        );
    }
}
