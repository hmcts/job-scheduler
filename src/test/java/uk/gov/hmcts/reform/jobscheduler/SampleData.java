package uk.gov.hmcts.reform.jobscheduler;

import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;

import java.time.ZonedDateTime;

public final class SampleData {

    private SampleData() {
    }

    public static String validJobJson() {
        return new JSONObject()
            .put("name", "my-job-name")
            .put("action", new JSONObject()
                .put("url", "https://my-cool-service.gov.uk/do-something")
                .put("method", "POST")
                .put("headers", new JSONObject()
                    .put("Authorization", "some-auth-token")
                )
                .put("body", "hello")
            )
            .put("trigger", new JSONObject()
                .put("frequency", "minute")
                .put("interval", 5)
                .put("start_date_time", "2042-08-11T12:11:00Z")
            )
            .toString();
    }

    public static Job validJob() {
        return new Job(
            "my-job-name",
            new HttpAction(
                "https://not-existing-service.gov.uk/do-stuff",
                HttpMethod.POST,
                ImmutableMap.of("Authorization", "token-goes-here"),
                null
            ),
            new Trigger(
                Trigger.Frequency.DAY,
                1,
                ZonedDateTime.now()
            )
        );
    }
}
