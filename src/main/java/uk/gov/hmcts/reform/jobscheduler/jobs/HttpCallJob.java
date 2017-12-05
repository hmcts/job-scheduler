package uk.gov.hmcts.reform.jobscheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCallJob implements Job {

    public static final String PARAMS_KEY = "params";
    private static final Logger logger = LoggerFactory.getLogger(HttpCallJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        // todo: send a http request based on data from job details
        logger.debug("Executing job: " + context.getJobDetail().getJobDataMap().getString(PARAMS_KEY));
    }
}
