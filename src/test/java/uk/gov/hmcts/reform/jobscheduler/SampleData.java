package uk.gov.hmcts.reform.jobscheduler;

import org.json.JSONObject;

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
}
