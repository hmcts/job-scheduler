package uk.gov.hmcts.reform.jobscheduler.model;

import java.util.List;

public class JobList {

    private final List<Job> data;

    public JobList(List<Job> data) {
        this.data = data;
    }

    public List<Job> getData() {
        return data;
    }
}
