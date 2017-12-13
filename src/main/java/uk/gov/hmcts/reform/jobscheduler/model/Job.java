package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.quartz.JobDetail;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;

public class Job {

    public final String name;
    public final HttpAction action;
    public final Trigger trigger;

    @JsonProperty("id")
    private String id;

    public Job(
        @JsonProperty("name") String name,
        @JsonProperty("action") HttpAction action,
        @JsonProperty("trigger") Trigger trigger
    ) {
        this.name = name;
        this.action = action;
        this.trigger = trigger;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Job fromJobDetail(JobDetail jobDetail, ActionSerializer serializer) {
        Job newJob = new Job(
            jobDetail.getDescription(),
            serializer.deserialize(jobDetail.getJobDataMap().getString(HttpCallJob.PARAMS_KEY)),
            null
        );

        newJob.setId(jobDetail.getKey().getName());

        return newJob;
    }
}
