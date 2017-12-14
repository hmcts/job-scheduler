package uk.gov.hmcts.reform.jobscheduler.model;

import java.util.List;

public class JobList {

    public final List<JobData> data;

    public JobList(List<JobData> data) {
        this.data = data;
    }
}
