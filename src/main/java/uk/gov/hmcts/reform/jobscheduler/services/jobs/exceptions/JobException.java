package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

public class JobException extends RuntimeException {

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}
