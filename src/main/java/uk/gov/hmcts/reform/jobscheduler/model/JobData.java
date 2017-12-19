package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public final class JobData {

    public final String id;

    @JsonUnwrapped
    public final Job job;

    public JobData(String id, Job job) {
        this.id = id;
        this.job = job;
    }
}
