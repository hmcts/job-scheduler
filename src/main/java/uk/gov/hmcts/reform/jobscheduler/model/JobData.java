package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobData {

    public final String id;
    public final Job job;

    public JobData(
        @JsonProperty("id") String id,
        @JsonProperty("job") Job job
    ) {
        this.id = id;
        this.job = job;
    }
}
