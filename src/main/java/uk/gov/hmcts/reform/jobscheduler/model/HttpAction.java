package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpMethod;

import java.util.Map;
import javax.validation.constraints.NotNull;

public class HttpAction {

    @NotBlank
    public final String url;

    @NotNull
    public final HttpMethod method;

    public final Map<String, String> headers;

    public final String body;

    public HttpAction(
        @JsonProperty("url") String url,
        @JsonProperty("method") HttpMethod method,
        @JsonProperty("headers") Map<String, String> headers,
        @JsonProperty("body") String body
    ) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }
}
