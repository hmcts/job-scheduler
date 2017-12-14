package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Job {

    public final String name;
    public final HttpAction action;
    public final Trigger trigger;

    public Job(
        @JsonProperty("name") String name,
        @JsonProperty("action") HttpAction action,
        @JsonProperty("trigger") Trigger trigger
    ) {
        this.name = name;
        this.action = action;
        this.trigger = trigger;
    }
}
