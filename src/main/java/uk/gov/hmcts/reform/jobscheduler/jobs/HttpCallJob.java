package uk.gov.hmcts.reform.jobscheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.ActionSerializer;

@Component
public class HttpCallJob implements Job {

    public static final String PARAMS_KEY = "params";
    private static final Logger logger = LoggerFactory.getLogger(HttpCallJob.class);

    private final ActionSerializer serializer;

    @Autowired
    public HttpCallJob(ActionSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void execute(JobExecutionContext context) {
        // todo: send a http request based on data from job details
        String json = context.getJobDetail().getJobDataMap().getString(PARAMS_KEY);
        HttpAction actionToPerform = serializer.deserialize(json);

        logger.info("Executing job: " + actionToPerform.url);
    }
}
