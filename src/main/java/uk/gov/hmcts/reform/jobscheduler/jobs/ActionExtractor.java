package uk.gov.hmcts.reform.jobscheduler.jobs;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;

import static uk.gov.hmcts.reform.jobscheduler.jobs.HttpCallJob.PARAMS_KEY;

@Component
public class ActionExtractor {
    private final ActionSerializer actionSerializer;

    public ActionExtractor(ActionSerializer actionSerializer) {
        this.actionSerializer = actionSerializer;
    }

    /**
     * Extracts action model from job execution context.
     */
    public HttpAction extract(JobExecutionContext ctx) {
        String json = ctx.getJobDetail().getJobDataMap().getString(PARAMS_KEY);
        return actionSerializer.deserialize(json);
    }
}
