package uk.gov.hmcts.reform.jobscheduler.model;

public final class JobData {

    public final String id;
    public final String name;
    public final HttpAction action;

    private JobData(
        String id,
        String name,
        HttpAction action
    ) {
        this.id = id;
        this.name = name;
        this.action = action;
    }

    public static JobData fromJob(String id, Job job) {
        return new JobData(id, job.name, job.action);
    }
}
