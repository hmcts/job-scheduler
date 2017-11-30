package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class HttpAction {

    public final String url;
    public final HttpMethod method;
    public final Map<String, String> headers;
    public final JsonNode body;

    public HttpAction(
        @JsonProperty("url") String url,
        @JsonProperty("method") HttpMethod method,
        @JsonProperty("headers") Map<String, String> headers,
        @JsonProperty("body") JsonNode body
    ) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }
}
