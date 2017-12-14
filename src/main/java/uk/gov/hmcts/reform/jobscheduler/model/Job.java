package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Job {

    @NotBlank
    public final String name;

    @Valid
    public final HttpAction action;

    @NotNull
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
