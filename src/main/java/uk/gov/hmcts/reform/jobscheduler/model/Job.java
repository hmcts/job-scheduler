package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.quartz.JobDetail;
import uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;

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

    public static Job fromJobDetail(JobDetail jobDetail, ActionSerializer serializer) {
        return new Job(
            jobDetail.getDescription(),
            serializer.deserialize(jobDetail.getJobDataMap().getString(HttpCallJob.PARAMS_KEY)),
            null
        );
    }
}
