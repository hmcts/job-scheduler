package uk.gov.hmcts.reform.jobscheduler.services.jobs;

/**
 * Keys that job-scheduler stores in job's JobDataMap.
 */
public final class JobDataKeys {

    // job-specific parameters
    public static final String PARAMS = "params";

    // number of execution attempt
    public static final String ATTEMPT = "attempt";

    private JobDataKeys() {
        // hiding default constructor
    }
}
